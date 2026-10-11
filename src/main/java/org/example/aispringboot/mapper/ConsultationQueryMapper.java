package org.example.aispringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.DTO.response.ConsultationSessionResponseDTO;

import java.util.List;

@Mapper
public interface ConsultationQueryMapper {
    String SESSION_FILTER = """
            FROM consultation_session s
            JOIN user u ON u.id = s.user_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) AS message_count, MAX(id) AS last_message_id
                FROM consultation_message
                GROUP BY session_id
            ) stats ON stats.session_id = s.id
            LEFT JOIN consultation_message lm ON lm.id = stats.last_message_id
            WHERE 1 = 1
            <if test="userId != null">
                AND s.user_id = #{userId}
            </if>
            <if test="emotionTag != null and emotionTag != ''">
                AND EXISTS (
                    SELECT 1 FROM consultation_message em
                    WHERE em.session_id = s.id AND em.emotion_tag = #{emotionTag}
                )
            </if>
            """;

    @Select("""
            <script>
            SELECT s.id, s.user_id,
                   COALESCE(NULLIF(u.nickname, ''), u.username) AS user_nickname,
                   s.session_title, s.started_at, s.started_at AS start_time,
                   lm.content AS last_message_content, lm.created_at AS last_message_time,
                   COALESCE(stats.message_count, 0) AS message_count,
                   TIMESTAMPDIFF(MINUTE, s.started_at, COALESCE(lm.created_at, s.started_at)) AS duration_minutes,
                   s.last_emotion_analysis, s.last_emotion_updated_at
            """ + SESSION_FILTER + """
            ORDER BY COALESCE(lm.created_at, s.started_at) DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<ConsultationSessionResponseDTO> selectSessionPage(
            @Param("userId") Long userId,
            @Param("emotionTag") String emotionTag,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
            <script>
            SELECT COUNT(*)
            """ + SESSION_FILTER + """
            </script>
            """)
    Long countSessions(@Param("userId") Long userId, @Param("emotionTag") String emotionTag);

    @Select("""
            SELECT id, session_id, sender_type,
                   CASE sender_type WHEN 1 THEN '用户' WHEN 2 THEN 'AI助手' ELSE '未知' END AS sender_type_desc,
                   message_type,
                   CASE message_type WHEN 1 THEN '文本' ELSE '未知' END AS message_type_desc,
                   content, emotion_tag, ai_model, created_at, CHAR_LENGTH(content) AS message_length
            FROM consultation_message
            WHERE session_id = #{sessionId}
            ORDER BY id ASC
            """)
    List<ConsultationMessageResponseDTO> selectMessages(@Param("sessionId") Long sessionId);
}
