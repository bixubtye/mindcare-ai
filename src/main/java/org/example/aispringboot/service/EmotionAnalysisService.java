package org.example.aispringboot.service;

import cn.hutool.json.JSONUtil;
import org.example.aispringboot.AiService.PromptManage;
import org.example.aispringboot.DTO.response.EmotionAnalysisAIResult;
import org.example.aispringboot.DTO.response.EmotionGardenResponseDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class EmotionAnalysisService {
    private static final Pattern EXPLICIT_CRISIS_PATTERN = Pattern.compile(
            "我(?:已经|正在|准备|决定|打算|马上|今晚|现在).{0,10}(?:自杀|跳楼|割腕|吞药|结束生命|伤害自己)"
                    + "|我无法保证自己安全|我控制不住.{0,8}(?:伤害自己|伤害别人)");
    private static final Pattern SEVERE_DISTRESS_PATTERN = Pattern.compile(
            "我不想活了|我想死|我活着没意思|我撑不下去了|我想结束生命|我(?:反复|一直)想自杀");
    private static final Pattern QUOTED_CONTEXT_PATTERN = Pattern.compile(
            "朋友说|同学说|他说|她说|电影里|小说里|新闻里|台词");

    private final ChatClient emotionClient;
    private final ConsultationSessionMapper consultationSessionMapper;
    private final ConsultationMessageService consultationMessageService;

    public EmotionAnalysisService(
            @Qualifier("emotion-analysis") ChatClient emotionClient,
            ConsultationSessionMapper consultationSessionMapper,
            ConsultationMessageService consultationMessageService) {
        this.emotionClient = emotionClient;
        this.consultationSessionMapper = consultationSessionMapper;
        this.consultationMessageService = consultationMessageService;
    }

    public EmotionGardenResponseDTO analyzeAndSave(Long sessionId, String currentMessage) {
        ConsultationSession session = consultationSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("咨询会话不存在");
        }

        String recentMessages = String.join("\n", consultationMessageService.getRecentUserMessages(sessionId, 6));
        String previousAnalysis = StringUtils.hasText(session.getLastEmotionAnalysis())
                ? session.getLastEmotionAnalysis()
                : "无";

        EmotionAnalysisAIResult aiResult = emotionClient.prompt()
                .system(PromptManage.EMOTION_ANALYSIS_SYSTEM_PROMPT)
                .options(OpenAiChatOptions.builder()
                        .extraBody(Map.of("enable_thinking", false)))
                .user(user -> user.text("""
                                当前用户消息：
                                {currentMessage}

                                最近的用户消息（按时间顺序）：
                                {recentMessages}

                                上一次情绪花园结果：
                                {previousAnalysis}
                                """)
                        .param("currentMessage", currentMessage)
                        .param("recentMessages", recentMessages)
                        .param("previousAnalysis", previousAnalysis))
                .call()
                .entity(EmotionAnalysisAIResult.class, spec -> spec
                        .useProviderStructuredOutput()
                        .validateSchema());

        if (aiResult == null) {
            throw new IllegalStateException("AI未返回情绪分析结果");
        }

        EmotionGardenResponseDTO result = normalizeResult(aiResult, currentMessage);
        session.setLastEmotionAnalysis(JSONUtil.toJsonStr(result));
        session.setLastEmotionUpdatedAt(LocalDateTime.now());
        consultationSessionMapper.updateById(session);
        return result;
    }

    EmotionGardenResponseDTO normalizeResult(
            EmotionAnalysisAIResult aiResult,
            String currentMessage) {
        String primaryEmotion = normalizeText(aiResult.primaryEmotion(), "复杂", 10);
        int emotionScore = clamp(aiResult.emotionScore(), 0, 100, 50);
        int aiRiskLevel = clamp(aiResult.riskLevel(), 0, 3, 0);
        int riskLevel = Math.max(aiRiskLevel, detectMinimumRisk(currentMessage));
        boolean showWarmReminder = riskLevel >= 2;
        boolean isNegative = riskLevel > 0 || emotionScore < 50;

        String suggestion = normalizeText(aiResult.suggestion(), "先停下来照顾一下此刻的自己。", 60);
        List<String> actions = normalizeActions(aiResult.improvementSuggestions());
        String riskDescription = null;

        if (riskLevel == 2) {
            suggestion = "请尽快让现实中可信任的人知道你现在的感受。";
            actions = List.of("联系信任的人陪伴或倾诉", "尽快预约专业心理支持", "拨打12356心理援助热线");
            riskDescription = "你现在可能正承受强烈痛苦，建议尽快联系学校心理咨询中心、可信任的人或拨打12356寻求专业支持。";
        } else if (riskLevel == 3) {
            suggestion = "请先把安全放在第一位，不要独自承受。";
            actions = List.of("立即联系可信任的人陪伴", "远离可能造成伤害的物品", "拨打120、110或12356寻求帮助");
            riskDescription = "如果你可能立即伤害自己或他人，请不要独处，立即联系可信任的人，并拨打120、110或12356寻求帮助。";
        }

        return new EmotionGardenResponseDTO(
                primaryEmotion,
                emotionScore,
                isNegative,
                riskLevel,
                suggestion,
                actions,
                riskDescription,
                showWarmReminder);
    }

    private int detectMinimumRisk(String message) {
        if (!StringUtils.hasText(message) || QUOTED_CONTEXT_PATTERN.matcher(message).find()) {
            return 0;
        }
        if (EXPLICIT_CRISIS_PATTERN.matcher(message).find()) {
            return 3;
        }
        return SEVERE_DISTRESS_PATTERN.matcher(message).find() ? 2 : 0;
    }

    private List<String> normalizeActions(List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return List.of("做3分钟缓慢呼吸", "记录此刻最明显的感受");
        }
        return actions.stream()
                .filter(StringUtils::hasText)
                .map(action -> normalizeText(action, "", 30))
                .filter(StringUtils::hasText)
                .distinct()
                .limit(3)
                .toList();
    }

    private String normalizeText(String value, String defaultValue, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        String normalized = value.trim();
        return normalized.length() <= maxLength
                ? normalized
                : normalized.substring(0, maxLength);
    }

    private int clamp(Integer value, int min, int max, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return Math.max(min, Math.min(max, value));
    }
}
