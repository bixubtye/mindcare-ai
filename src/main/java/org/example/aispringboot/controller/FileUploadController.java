package org.example.aispringboot.controller;

import jakarta.annotation.Resource;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.FileStorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {
    @Resource
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('2')")
    public Result<String> upload(
            @RequestParam MultipartFile file,
            @RequestParam String businessType,
            @RequestParam String businessId,
            @RequestParam String businessField) {
        return Result.ok(fileStorageService.upload(file, businessType, businessId, businessField));
    }
}
