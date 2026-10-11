package org.example.aispringboot.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationSessionResponseDTO {
    private Long id;
    private Long userId;
    private String userNickname;
    private String sessionTitle;
    private LocalDateTime startedAt;
    private LocalDateTime startTime;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Integer messageCount;
    private Long durationMinutes;
    private String lastEmotionAnalysis;
    private LocalDateTime lastEmotionUpdatedAt;
}
