package org.example.aispringboot.service;

import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.response.DataAnalyticsResponseDTO;
import org.example.aispringboot.mapper.DataAnalyticsMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataAnalyticsService {
    private static final int TREND_DAYS = 7;

    @Resource
    private DataAnalyticsMapper dataAnalyticsMapper;

    public DataAnalyticsResponseDTO getOverview() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(TREND_DAYS - 1L);
        DataAnalyticsResponseDTO.ConsultationStats consultationStats =
                dataAnalyticsMapper.selectConsultationStats();
        consultationStats.setDailyTrend(fillConsultationTrend(
                dataAnalyticsMapper.selectConsultationDailyTrend(startDate, endDate), startDate));
        return DataAnalyticsResponseDTO.builder()
                .systemOverview(dataAnalyticsMapper.selectSystemOverview())
                .emotionTrend(fillEmotionTrend(
                        dataAnalyticsMapper.selectEmotionTrend(startDate, endDate), startDate))
                .consultationStats(consultationStats)
                .userActivity(fillUserActivity(
                        dataAnalyticsMapper.selectUserActivity(startDate, endDate), startDate))
                .build();
    }

    private List<DataAnalyticsResponseDTO.EmotionTrend> fillEmotionTrend(
            List<DataAnalyticsResponseDTO.EmotionTrend> source, LocalDate startDate) {
        Map<LocalDate, DataAnalyticsResponseDTO.EmotionTrend> indexed = source.stream()
                .collect(Collectors.toMap(DataAnalyticsResponseDTO.EmotionTrend::getDate, Function.identity()));
        List<DataAnalyticsResponseDTO.EmotionTrend> result = new ArrayList<>();
        for (int day = 0; day < TREND_DAYS; day++) {
            LocalDate date = startDate.plusDays(day);
            result.add(indexed.getOrDefault(date,
                    new DataAnalyticsResponseDTO.EmotionTrend(date, 0D, 0L)));
        }
        return result;
    }

    private List<DataAnalyticsResponseDTO.ConsultationDailyTrend> fillConsultationTrend(
            List<DataAnalyticsResponseDTO.ConsultationDailyTrend> source, LocalDate startDate) {
        Map<LocalDate, DataAnalyticsResponseDTO.ConsultationDailyTrend> indexed = source.stream()
                .collect(Collectors.toMap(
                        DataAnalyticsResponseDTO.ConsultationDailyTrend::getDate, Function.identity()));
        List<DataAnalyticsResponseDTO.ConsultationDailyTrend> result = new ArrayList<>();
        for (int day = 0; day < TREND_DAYS; day++) {
            LocalDate date = startDate.plusDays(day);
            result.add(indexed.getOrDefault(date,
                    new DataAnalyticsResponseDTO.ConsultationDailyTrend(date, 0L, 0L)));
        }
        return result;
    }

    private List<DataAnalyticsResponseDTO.UserActivity> fillUserActivity(
            List<DataAnalyticsResponseDTO.UserActivity> source, LocalDate startDate) {
        Map<LocalDate, DataAnalyticsResponseDTO.UserActivity> indexed = source.stream()
                .collect(Collectors.toMap(DataAnalyticsResponseDTO.UserActivity::getDate, Function.identity()));
        List<DataAnalyticsResponseDTO.UserActivity> result = new ArrayList<>();
        for (int day = 0; day < TREND_DAYS; day++) {
            LocalDate date = startDate.plusDays(day);
            result.add(indexed.getOrDefault(date,
                    new DataAnalyticsResponseDTO.UserActivity(date, 0L, 0L, 0L, 0L)));
        }
        return result;
    }
}
