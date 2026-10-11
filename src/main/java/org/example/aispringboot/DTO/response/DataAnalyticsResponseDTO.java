package org.example.aispringboot.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DataAnalyticsResponseDTO {
    private SystemOverview systemOverview;
    private List<EmotionTrend> emotionTrend;
    private ConsultationStats consultationStats;
    private List<UserActivity> userActivity;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemOverview {
        private Long totalUsers;
        private Long activeUsers;
        private Long totalDiaries;
        private Long todayNewDiaries;
        private Long totalSessions;
        private Long todayNewSessions;
        private Double avgMoodScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmotionTrend {
        private LocalDate date;
        private Double avgMoodScore;
        private Long recordCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultationStats {
        private Long totalSessions;
        private Double avgDurationMinutes;
        private List<ConsultationDailyTrend> dailyTrend;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultationDailyTrend {
        private LocalDate date;
        private Long sessionCount;
        private Long userCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivity {
        private LocalDate date;
        private Long activeUsers;
        private Long newUsers;
        private Long diaryUsers;
        private Long consultationUsers;
    }
}
