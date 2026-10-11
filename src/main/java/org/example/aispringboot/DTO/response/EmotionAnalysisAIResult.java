package org.example.aispringboot.DTO.response;

import java.util.List;

/**
 * AI返回的原始情绪分析。最终风险提示由后端统一生成。
 */
public record EmotionAnalysisAIResult(
        String primaryEmotion,
        Integer emotionScore,
        Integer riskLevel,
        String suggestion,
        List<String> improvementSuggestions
) {
}
