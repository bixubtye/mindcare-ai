package org.example.aispringboot.DTO.response;

import java.util.List;

/**
 * 保存到会话并返回给前端的最新情绪花园。
 */
public record EmotionGardenResponseDTO(
        String primaryEmotion,
        Integer emotionScore,
        Boolean isNegative,
        Integer riskLevel,
        String suggestion,
        List<String> improvementSuggestions,
        String riskDescription,
        Boolean showWarmReminder
) {
}
