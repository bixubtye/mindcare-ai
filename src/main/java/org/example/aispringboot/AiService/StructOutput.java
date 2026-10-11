package org.example.aispringboot.AiService;

public class StructOutput {
    public record StreamChatSession(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            String status,
            Integer messageCount
    ){}
}