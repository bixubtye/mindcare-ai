package org.example.aispringboot.DTO.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmotionDiaryCommandDTO {
    @NotNull(message = "日记日期不能为空")
    private LocalDate diaryDate;

    @NotNull(message = "情绪评分不能为空")
    @Min(value = 1, message = "情绪评分必须在1到10之间")
    @Max(value = 10, message = "情绪评分必须在1到10之间")
    private Integer moodScore;

    @NotBlank(message = "主要情绪不能为空")
    @Size(max = 50, message = "主要情绪不能超过50个字符")
    private String dominantEmotion;

    private String emotionTriggers;
    private String diaryContent;

    @Min(value = 1, message = "睡眠质量必须在1到5之间")
    @Max(value = 5, message = "睡眠质量必须在1到5之间")
    private Integer sleepQuality;

    @Min(value = 1, message = "压力水平必须在1到5之间")
    @Max(value = 5, message = "压力水平必须在1到5之间")
    private Integer stressLevel;
}
