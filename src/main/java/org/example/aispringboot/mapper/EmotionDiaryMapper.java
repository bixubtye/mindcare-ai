package org.example.aispringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.aispringboot.DTO.command.EmotionDiaryCommandDTO;
import org.example.aispringboot.DTO.response.EmotionDiaryResponseDTO;
import org.example.aispringboot.entity.EmotionDiary;

import java.util.List;

@Mapper
public interface EmotionDiaryMapper extends BaseMapper<EmotionDiary> {
    String DIARY_FILTER = """
            FROM emotion_diary d
            JOIN user u ON u.id = d.user_id
            LEFT JOIN ai_analysis_task t ON t.id = (
                SELECT MAX(t2.id) FROM ai_analysis_task t2 WHERE t2.diary_id = d.id
            )
            WHERE 1 = 1
            <if test="userId != null">
                AND d.user_id = #{userId}
            </if>
            <if test="minMoodScore != null">
                AND d.mood_score &gt;= #{minMoodScore}
            </if>
            <if test="maxMoodScore != null">
                AND d.mood_score &lt;= #{maxMoodScore}
            </if>
            <if test="dominantEmotion != null and dominantEmotion != ''">
                AND d.dominant_emotion LIKE CONCAT('%', #{dominantEmotion}, '%')
            </if>
            """;

    @Select("""
            <script>
            SELECT d.id, d.user_id, u.username, u.nickname, d.diary_date, d.mood_score,
                   d.dominant_emotion, d.emotion_triggers, d.diary_content,
                   d.sleep_quality, d.stress_level, d.ai_emotion_analysis,
                   d.ai_analysis_updated_at, t.status AS ai_analysis_status,
                   d.created_at, d.updated_at
            """ + DIARY_FILTER + """
            ORDER BY d.diary_date DESC, d.id DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<EmotionDiaryResponseDTO> selectDiaryPage(
            @Param("userId") Long userId,
            @Param("minMoodScore") Integer minMoodScore,
            @Param("maxMoodScore") Integer maxMoodScore,
            @Param("dominantEmotion") String dominantEmotion,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
            <script>
            SELECT COUNT(*)
            """ + DIARY_FILTER + """
            </script>
            """)
    Long countDiaries(@Param("userId") Long userId,
                      @Param("minMoodScore") Integer minMoodScore,
                      @Param("maxMoodScore") Integer maxMoodScore,
                      @Param("dominantEmotion") String dominantEmotion);

    @Update("""
            UPDATE emotion_diary
            SET mood_score = #{command.moodScore},
                dominant_emotion = #{command.dominantEmotion},
                emotion_triggers = #{command.emotionTriggers},
                diary_content = #{command.diaryContent},
                sleep_quality = #{command.sleepQuality},
                stress_level = #{command.stressLevel},
                ai_emotion_analysis = NULL,
                ai_analysis_updated_at = NULL,
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateDiary(@Param("id") Long id, @Param("command") EmotionDiaryCommandDTO command);

    @Select("""
            SELECT d.id, d.user_id, u.username, u.nickname, d.diary_date, d.mood_score,
                   d.dominant_emotion, d.emotion_triggers, d.diary_content,
                   d.sleep_quality, d.stress_level, d.ai_emotion_analysis,
                   d.ai_analysis_updated_at, t.status AS ai_analysis_status,
                   d.created_at, d.updated_at
            FROM emotion_diary d
            JOIN user u ON u.id = d.user_id
            LEFT JOIN ai_analysis_task t ON t.id = (
                SELECT MAX(t2.id) FROM ai_analysis_task t2 WHERE t2.diary_id = d.id
            )
            WHERE d.id = #{id}
            """)
    EmotionDiaryResponseDTO selectDiaryDetail(@Param("id") Long id);
}
