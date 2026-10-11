package org.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.command.EmotionDiaryCommandDTO;
import org.example.aispringboot.DTO.response.EmotionDiaryResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.entity.AiAnalysisTask;
import org.example.aispringboot.entity.EmotionDiary;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.AiAnalysisTaskMapper;
import org.example.aispringboot.mapper.EmotionDiaryMapper;
import org.example.aispringboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class EmotionDiaryService {
    @Resource
    private EmotionDiaryMapper emotionDiaryMapper;

    @Resource
    private AiAnalysisTaskMapper aiAnalysisTaskMapper;

    @Transactional
    public EmotionDiaryResponseDTO createOrUpdate(EmotionDiaryCommandDTO commandDTO) {
        Long userId = CurrentUserUtil.getUserId();
        EmotionDiary existing = emotionDiaryMapper.selectOne(
                new LambdaQueryWrapper<EmotionDiary>()
                        .eq(EmotionDiary::getUserId, userId)
                        .eq(EmotionDiary::getDiaryDate, commandDTO.getDiaryDate()));
        Long diaryId;
        if (existing == null) {
            LocalDateTime now = LocalDateTime.now();
            EmotionDiary diary = EmotionDiary.builder()
                    .userId(userId)
                    .diaryDate(commandDTO.getDiaryDate())
                    .moodScore(commandDTO.getMoodScore())
                    .dominantEmotion(commandDTO.getDominantEmotion().trim())
                    .emotionTriggers(commandDTO.getEmotionTriggers())
                    .diaryContent(commandDTO.getDiaryContent())
                    .sleepQuality(commandDTO.getSleepQuality())
                    .stressLevel(commandDTO.getStressLevel())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            emotionDiaryMapper.insert(diary);
            diaryId = diary.getId();
        } else {
            emotionDiaryMapper.updateDiary(existing.getId(), commandDTO);
            diaryId = existing.getId();
        }
        createAnalysisTask(diaryId, userId);
        return emotionDiaryMapper.selectDiaryDetail(diaryId);
    }

    public PageResponseDTO<EmotionDiaryResponseDTO> getAdminPage(
            Long current, Long size, Long userId, Integer minMoodScore,
            Integer maxMoodScore, String dominantEmotion) {
        long pageNumber = current == null || current < 1 ? 1 : current;
        long pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        String emotion = StringUtils.hasText(dominantEmotion) ? dominantEmotion.trim() : null;
        if (minMoodScore != null && maxMoodScore != null && minMoodScore > maxMoodScore) {
            throw new BusinessException("情绪评分范围不正确");
        }
        var records = emotionDiaryMapper.selectDiaryPage(
                userId, minMoodScore, maxMoodScore, emotion, (pageNumber - 1) * pageSize, pageSize);
        Long total = emotionDiaryMapper.countDiaries(userId, minMoodScore, maxMoodScore, emotion);
        return new PageResponseDTO<>(records, total, pageNumber, pageSize);
    }

    @Transactional
    public void deleteDiary(Long id) {
        if (emotionDiaryMapper.deleteById(id) == 0) {
            throw new BusinessException("情绪日记不存在");
        }
    }

    private void createAnalysisTask(Long diaryId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        AiAnalysisTask task = AiAnalysisTask.builder()
                .diaryId(diaryId)
                .userId(userId)
                .status("PENDING")
                .taskType("AUTO")
                .priority(2)
                .retryCount(0)
                .maxRetryCount(3)
                .createdAt(now)
                .updatedAt(now)
                .build();
        aiAnalysisTaskMapper.insert(task);
    }
}
