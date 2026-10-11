package org.example.aispringboot.service;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.DTO.response.ConsultationSessionResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.ConsultationQueryMapper;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.example.aispringboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ConsultationQueryService {
    @Resource
    private ConsultationQueryMapper consultationQueryMapper;

    @Resource
    private ConsultationSessionMapper consultationSessionMapper;

    public PageResponseDTO<ConsultationSessionResponseDTO> getSessionPage(
            Long currentPage, Long size, Long pageNum, Long pageSize, String emotionTag) {
        long selectedPage = normalizePage(pageNum != null ? pageNum : currentPage);
        long selectedSize = normalizeSize(pageSize != null ? pageSize : size);
        Long userId = CurrentUserUtil.isAdmin() ? null : CurrentUserUtil.getUserId();
        String selectedEmotionTag = StringUtils.hasText(emotionTag) ? emotionTag.trim() : null;
        List<ConsultationSessionResponseDTO> records = consultationQueryMapper.selectSessionPage(
                userId, selectedEmotionTag, (selectedPage - 1) * selectedSize, selectedSize);
        Long total = consultationQueryMapper.countSessions(userId, selectedEmotionTag);
        return new PageResponseDTO<>(records, total, selectedPage, selectedSize);
    }

    public List<ConsultationMessageResponseDTO> getSessionMessages(String sessionId) {
        ConsultationSession session = getAccessibleSession(parseSessionId(sessionId));
        return consultationQueryMapper.selectMessages(session.getId());
    }

    public Object getSessionEmotion(String sessionId) {
        ConsultationSession session = getAccessibleSession(parseSessionId(sessionId));
        if (!StringUtils.hasText(session.getLastEmotionAnalysis())) {
            return Collections.emptyMap();
        }
        try {
            return JSONUtil.parseObj(session.getLastEmotionAnalysis());
        } catch (Exception exception) {
            return Map.of("raw", session.getLastEmotionAnalysis());
        }
    }

    @Transactional
    public void deleteSession(String sessionId) {
        ConsultationSession session = getAccessibleSession(parseSessionId(sessionId));
        consultationSessionMapper.deleteById(session.getId());
    }

    private ConsultationSession getAccessibleSession(Long sessionId) {
        ConsultationSession session = consultationSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("咨询会话不存在");
        }
        if (!CurrentUserUtil.isAdmin() && !CurrentUserUtil.getUserId().equals(session.getUserId())) {
            throw new BusinessException("无权访问该咨询会话");
        }
        return session;
    }

    private Long parseSessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new BusinessException("会话ID不能为空");
        }
        String value = sessionId.startsWith("session_") ? sessionId.substring(8) : sessionId;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new BusinessException("会话ID格式错误");
        }
    }

    private long normalizePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long normalizeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
