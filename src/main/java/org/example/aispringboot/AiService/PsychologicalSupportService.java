package org.example.aispringboot.AiService;

import org.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.service.ConsultationMessageService;
import org.example.aispringboot.service.ConsultationSessionService;
import org.example.aispringboot.service.EmotionAnalysisService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class PsychologicalSupportService {
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;
    @Autowired
    private ConsultationSessionService consultationSessionService;
    @Autowired
    private ConsultationMessageService consultationMessageService;
    @Autowired
    private EmotionAnalysisService emotionAnalysisService;
    public StructOutput.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //插入数据库
        ConsultationSession session = consultationSessionService.createSession(userId, createDTO);
        //把初始消息保存到message表
        consultationMessageService.createMessage(session.getId(), createDTO.getInitialMessage(), "null");
        //创建会话信息
        String sessionId = "session_" + session.getId().toString();
        return new StructOutput.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000,
                "active",
                1
        );
    }
    public Flux<String> streamPsychologicalChat(String sessionId, String message) {
        return Flux.defer(() -> {
            Long dbsessionId = getSessionId(sessionId);
            if(dbsessionId==null)
            {
                return Flux.error(new RuntimeException("sessionId格式错误"));
            }
            //是否为初始消息
            boolean isInitialMessage = false;

            //检查是否为初始消息，避免重复保存
            Integer messageCount = consultationMessageService.getMessageCount(dbsessionId);
            if(messageCount==1)
            {
               ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbsessionId);
               if(lastMessage!=null&&lastMessage.getMessageType()==1&&message.equals(lastMessage.getContent()))
               {
                   isInitialMessage = true;
               }
            }
            if(!isInitialMessage)
            {
                //插入数据到数据库
                consultationMessageService.createMessage(dbsessionId, message, "null");
            }
            //开始流式对话
            //生成对话记忆管理
            String conversationId = "conversation_" + sessionId;
            //构建系统提示词
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));
            //存储AI的相应
            StringBuilder aiResponse = new StringBuilder();
            Flux<String> contentFlux = chatClient.prompt(prompt)
                    .user(message)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(aiResponse::append);

            Mono<String> finishTurn = Mono.fromRunnable(() -> {
                        String completeResponse = aiResponse.toString();
                        consultationMessageService.saveAiMessage(
                                dbsessionId,
                                completeResponse,
                                "deepseek-ai/DeepSeek-V4-Flash");
                        try {
                            emotionAnalysisService.analyzeAndSave(dbsessionId, message);
                        } catch (Exception exception) {
                            // 情绪分析失败不应影响已经生成的心理支持回复。
                            log.error("情绪分析失败，sessionId={}", dbsessionId, exception);
                        }
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .then(Mono.empty());

            // 只有AI回复保存和情绪分析完成后流才结束，Controller随后才会发送done。
            return contentFlux.concatWith(finishTurn);

        });

    }
    //获取参数中的sessionId
    public Long getSessionId(String sessionId) {
        if(sessionId!=null&&sessionId.startsWith("session_"))
        {
            String sessionIdStr = sessionId.substring("session_".length());
            return Long.parseLong(sessionIdStr);
        }
        return null;
    }
}
