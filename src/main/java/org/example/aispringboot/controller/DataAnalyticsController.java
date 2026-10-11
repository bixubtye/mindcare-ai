package org.example.aispringboot.controller;

import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.response.DataAnalyticsResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.DataAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-analytics")
public class DataAnalyticsController {
    @Resource
    private DataAnalyticsService dataAnalyticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('2')")
    public Result<DataAnalyticsResponseDTO> getOverview() {
        return Result.ok(dataAnalyticsService.getOverview());
    }
}
