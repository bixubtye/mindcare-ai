package org.example.aispringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.aispringboot.DTO.response.DataAnalyticsResponseDTO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DataAnalyticsMapper {
    @Select("""
            SELECT
                (SELECT COUNT(*) FROM user WHERE user_type = 1) AS total_users,
                (SELECT COUNT(*) FROM user WHERE user_type = 1 AND status = 1) AS active_users,
                (SELECT COUNT(*) FROM emotion_diary) AS total_diaries,
                (SELECT COUNT(*) FROM emotion_diary WHERE diary_date = CURDATE()) AS today_new_diaries,
                (SELECT COUNT(*) FROM consultation_session) AS total_sessions,
                (SELECT COUNT(*) FROM consultation_session WHERE DATE(started_at) = CURDATE()) AS today_new_sessions,
                (SELECT COALESCE(ROUND(AVG(mood_score), 2), 0) FROM emotion_diary) AS avg_mood_score
            """)
    DataAnalyticsResponseDTO.SystemOverview selectSystemOverview();

    @Select("""
            SELECT diary_date AS date, ROUND(AVG(mood_score), 2) AS avg_mood_score,
                   COUNT(*) AS record_count
            FROM emotion_diary
            WHERE diary_date BETWEEN #{startDate} AND #{endDate}
            GROUP BY diary_date
            ORDER BY diary_date
            """)
    List<DataAnalyticsResponseDTO.EmotionTrend> selectEmotionTrend(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("""
            SELECT COUNT(*) AS total_sessions,
                   COALESCE(ROUND(AVG(TIMESTAMPDIFF(MINUTE, s.started_at,
                       COALESCE(last_message.last_time, s.started_at))), 2), 0) AS avg_duration_minutes
            FROM consultation_session s
            LEFT JOIN (
                SELECT session_id, MAX(created_at) AS last_time
                FROM consultation_message
                GROUP BY session_id
            ) last_message ON last_message.session_id = s.id
            """)
    DataAnalyticsResponseDTO.ConsultationStats selectConsultationStats();

    @Select("""
            SELECT DATE(started_at) AS date, COUNT(*) AS session_count,
                   COUNT(DISTINCT user_id) AS user_count
            FROM consultation_session
            WHERE DATE(started_at) BETWEEN #{startDate} AND #{endDate}
            GROUP BY DATE(started_at)
            ORDER BY DATE(started_at)
            """)
    List<DataAnalyticsResponseDTO.ConsultationDailyTrend> selectConsultationDailyTrend(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("""
            SELECT dates.activity_date AS date,
                   (
                       SELECT COUNT(DISTINCT active_user.user_id)
                       FROM (
                           SELECT user_id, diary_date AS activity_date FROM emotion_diary
                           UNION ALL
                           SELECT user_id, DATE(started_at) AS activity_date FROM consultation_session
                       ) active_user
                       WHERE active_user.activity_date = dates.activity_date
                   ) AS active_users,
                   (SELECT COUNT(*) FROM user u WHERE DATE(u.created_at) = dates.activity_date) AS new_users,
                   (SELECT COUNT(DISTINCT d.user_id) FROM emotion_diary d
                       WHERE d.diary_date = dates.activity_date) AS diary_users,
                   (SELECT COUNT(DISTINCT s.user_id) FROM consultation_session s
                       WHERE DATE(s.started_at) = dates.activity_date) AS consultation_users
            FROM (
                SELECT diary_date AS activity_date FROM emotion_diary
                WHERE diary_date BETWEEN #{startDate} AND #{endDate}
                UNION
                SELECT DATE(started_at) AS activity_date FROM consultation_session
                WHERE DATE(started_at) BETWEEN #{startDate} AND #{endDate}
                UNION
                SELECT DATE(created_at) AS activity_date FROM user
                WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate}
            ) dates
            ORDER BY dates.activity_date
            """)
    List<DataAnalyticsResponseDTO.UserActivity> selectUserActivity(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
