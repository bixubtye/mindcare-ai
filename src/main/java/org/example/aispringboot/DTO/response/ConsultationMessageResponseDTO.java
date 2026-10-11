package org.example.aispringboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationMessageResponseDTO {

    private Long id;

    private Long sessionId;

    private Integer senderType;

    private String senderTypeDesc;

    private Integer messageType;

    private String messageTypeDesc;

    private String content;

    private String emotionTag;

    private String aiModel;

    private LocalDateTime createdAt;
    private Integer messageLength;

    /**
     * 计算消息长度
     */
    public void calculateMessageLength() {
        this.messageLength = content!=null?content.length():0;
    }
}