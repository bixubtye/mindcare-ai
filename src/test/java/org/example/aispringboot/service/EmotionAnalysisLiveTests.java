package org.example.aispringboot.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.aispringboot.DTO.response.EmotionGardenResponseDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.example.aispringboot.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_LIVE_AI_TESTS", matches = "true")
@Transactional
class EmotionAnalysisLiveTests {
    private final EmotionAnalysisService emotionAnalysisService;
    private final ConsultationMessageService consultationMessageService;
    private final ConsultationSessionMapper consultationSessionMapper;
    private final UserMapper userMapper;

    @Autowired
    EmotionAnalysisLiveTests(
            EmotionAnalysisService emotionAnalysisService,
            ConsultationMessageService consultationMessageService,
            ConsultationSessionMapper consultationSessionMapper,
            UserMapper userMapper) {
        this.emotionAnalysisService = emotionAnalysisService;
        this.consultationMessageService = consultationMessageService;
        this.consultationSessionMapper = consultationSessionMapper;
        this.userMapper = userMapper;
    }

    @Test
    void providerResultIsSavedAsLatestSessionEmotion() {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().last("limit 1"));
        assumeTrue(user != null, "数据库中需要至少一个用户用于事务内联调");

        String message = "最近考试压力有点大，昨晚没睡好，但没有伤害自己的想法。";
        ConsultationSession session = ConsultationSession.builder()
                .userId(user.getId())
                .sessionTitle("情绪花园联调会话")
                .startedAt(LocalDateTime.now())
                .build();
        consultationSessionMapper.insert(session);
        consultationMessageService.createMessage(session.getId(), message, null);

        EmotionGardenResponseDTO result = emotionAnalysisService.analyzeAndSave(session.getId(), message);
        ConsultationSession savedSession = consultationSessionMapper.selectById(session.getId());
        JSONObject savedJson = JSONUtil.parseObj(savedSession.getLastEmotionAnalysis());

        assertThat(result.primaryEmotion()).isNotBlank();
        assertThat(result.emotionScore()).isBetween(0, 100);
        assertThat(result.riskLevel()).isBetween(0, 1);
        assertThat(result.showWarmReminder()).isFalse();
        assertThat(savedJson.getStr("primaryEmotion")).isEqualTo(result.primaryEmotion());
        assertThat(savedJson.getBool("showWarmReminder")).isFalse();
        assertThat(savedSession.getLastEmotionUpdatedAt()).isNotNull();
    }
}
