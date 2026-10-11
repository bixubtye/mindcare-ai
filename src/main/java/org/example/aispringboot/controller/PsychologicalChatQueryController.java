package org.example.aispringboot.controller;

import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.DTO.response.ConsultationSessionResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.ConsultationQueryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChatQueryController {
    @Resource
    private ConsultationQueryService consultationQueryService;

    @GetMapping("/sessions")
    public Result<PageResponseDTO<ConsultationSessionResponseDTO>> getSessionPage(
            @RequestParam(required = false) Long currentPage,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String emotionTag) {
        return Result.ok(consultationQueryService.getSessionPage(
                currentPage, size, pageNum, pageSize, emotionTag));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ConsultationMessageResponseDTO>> getSessionMessages(@PathVariable String sessionId) {
        return Result.ok(consultationQueryService.getSessionMessages(sessionId));
    }

    @GetMapping("/session/{sessionId}/emotion")
    public Result<Object> getSessionEmotion(@PathVariable String sessionId) {
        return Result.ok(consultationQueryService.getSessionEmotion(sessionId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId) {
        consultationQueryService.deleteSession(sessionId);
        return Result.ok();
    }
}
