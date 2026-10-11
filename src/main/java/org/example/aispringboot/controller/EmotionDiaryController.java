package org.example.aispringboot.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aispringboot.DTO.command.EmotionDiaryCommandDTO;
import org.example.aispringboot.DTO.response.EmotionDiaryResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.EmotionDiaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emotion-diary")
public class EmotionDiaryController {
    @Resource
    private EmotionDiaryService emotionDiaryService;

    @PostMapping
    public Result<EmotionDiaryResponseDTO> createOrUpdate(
            @Valid @RequestBody EmotionDiaryCommandDTO commandDTO) {
        return Result.ok(emotionDiaryService.createOrUpdate(commandDTO));
    }

    @GetMapping("/admin/page")
    @PreAuthorize("hasRole('2')")
    public Result<PageResponseDTO<EmotionDiaryResponseDTO>> getAdminPage(
            @RequestParam(required = false) Long current,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer minMoodScore,
            @RequestParam(required = false) Integer maxMoodScore,
            @RequestParam(required = false) String dominantEmotion) {
        return Result.ok(emotionDiaryService.getAdminPage(
                current, size, userId, minMoodScore, maxMoodScore, dominantEmotion));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('2')")
    public Result<Void> deleteDiary(@PathVariable Long id) {
        emotionDiaryService.deleteDiary(id);
        return Result.ok();
    }
}
