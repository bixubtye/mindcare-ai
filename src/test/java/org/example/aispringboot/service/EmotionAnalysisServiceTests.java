package org.example.aispringboot.service;

import cn.hutool.json.JSONUtil;
import org.example.aispringboot.DTO.response.EmotionAnalysisAIResult;
import org.example.aispringboot.DTO.response.EmotionGardenResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmotionAnalysisServiceTests {
    private final EmotionAnalysisService service = new EmotionAnalysisService(null, null, null);

    @Test
    void normalNegativeEmotionDoesNotShowWarmReminder() {
        EmotionGardenResponseDTO result = service.normalizeResult(
                new EmotionAnalysisAIResult(
                        "焦虑",
                        42,
                        1,
                        "先完成今天最重要的一件小事。",
                        List.of("做3分钟缓慢呼吸")),
                "最近考试压力很大，昨晚没有睡好");

        assertThat(result.riskLevel()).isEqualTo(1);
        assertThat(result.showWarmReminder()).isFalse();
        assertThat(result.riskDescription()).isNull();
    }

    @Test
    void severeDistressAlwaysShowsWarmReminder() {
        EmotionGardenResponseDTO result = service.normalizeResult(
                new EmotionAnalysisAIResult("绝望", 10, 0, "休息一下", List.of("深呼吸")),
                "我真的撑不下去了，我不想活了");

        assertThat(result.riskLevel()).isEqualTo(2);
        assertThat(result.showWarmReminder()).isTrue();
        assertThat(result.riskDescription()).contains("12356");
    }

    @Test
    void explicitPlanIsTreatedAsCrisis() {
        EmotionGardenResponseDTO result = service.normalizeResult(
                new EmotionAnalysisAIResult("悲伤", 15, 1, "休息一下", List.of("深呼吸")),
                "我今晚准备吞药结束生命");

        assertThat(result.riskLevel()).isEqualTo(3);
        assertThat(result.showWarmReminder()).isTrue();
        assertThat(result.suggestion()).contains("安全");
    }

    @Test
    void negatedOrQuotedTextDoesNotTriggerRuleUpgrade() {
        EmotionGardenResponseDTO negated = service.normalizeResult(
                new EmotionAnalysisAIResult("平静", 70, 0, "保持规律作息", List.of("散步")),
                "我没有自杀想法，也不会伤害自己");
        EmotionGardenResponseDTO quoted = service.normalizeResult(
                new EmotionAnalysisAIResult("平静", 70, 0, "保持规律作息", List.of("散步")),
                "电影里的台词是我想死");

        assertThat(negated.riskLevel()).isZero();
        assertThat(quoted.riskLevel()).isZero();
    }

    @Test
    void gardenRecordCanBeStoredAsJson() {
        EmotionGardenResponseDTO result = service.normalizeResult(
                new EmotionAnalysisAIResult("开心", 85, 0, "继续保持", List.of("记录好心情")),
                "今天感觉很好");

        String json = JSONUtil.toJsonStr(result);

        assertThat(json).contains("\"primaryEmotion\":\"开心\"");
        assertThat(json).contains("\"showWarmReminder\":false");
    }
}
