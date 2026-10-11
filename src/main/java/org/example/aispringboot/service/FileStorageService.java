package org.example.aispringboot.service;

import jakarta.annotation.Resource;
import org.example.aispringboot.common.ResultCode;
import org.example.aispringboot.config.FileStorageConfig;
import org.example.aispringboot.entity.SysFileInfo;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.SysFileInfoMapper;
import org.example.aispringboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png");

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    @Transactional
    public String upload(MultipartFile file, String businessType, String businessId, String businessField) {
        validateFile(file);
        if (!"ARTICLE".equalsIgnoreCase(businessType)) {
            throw new BusinessException("不支持的业务类型");
        }
        if (!StringUtils.hasText(businessId) || !StringUtils.hasText(businessField)) {
            throw new BusinessException("文件业务信息不能为空");
        }

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativeDirectory = "files/bussiness/" + businessType.toLowerCase(Locale.ROOT);
        Path directory = FileStorageConfig.STORAGE_ROOT.resolve(relativeDirectory).normalize();
        Path target = directory.resolve(fileName).normalize();
        if (!target.startsWith(FileStorageConfig.STORAGE_ROOT)) {
            throw new BusinessException("文件路径不合法");
        }

        try {
            Files.createDirectories(directory);
            file.transferTo(target);
        } catch (IOException exception) {
            throw new BusinessException(ResultCode.FILE_SAVE_FAILED.getMsg());
        }

        String filePath = "/" + relativeDirectory.replace('\\', '/') + "/" + fileName;
        SysFileInfo fileInfo = SysFileInfo.builder()
                .originalName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .fileType("IMG")
                .businessType(businessType.toUpperCase(Locale.ROOT))
                .businessId(businessId)
                .businessField(businessField)
                .uploadUserId(CurrentUserUtil.getUserId())
                .isTemp(0)
                .status(1)
                .createTime(LocalDateTime.now())
                .build();
        try {
            sysFileInfoMapper.insert(fileInfo);
        } catch (RuntimeException exception) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException ignored) {
            }
            throw exception;
        }
        return filePath;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED.getMsg());
        }
        if (!IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException("仅支持JPG和PNG图片");
        }
    }

    private String getExtension(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            throw new BusinessException(ResultCode.FILE_NAME_INVALID.getMsg());
        }
        int separator = originalName.lastIndexOf('.');
        String extension = separator >= 0 ? originalName.substring(separator).toLowerCase(Locale.ROOT) : "";
        if (!Set.of(".jpg", ".jpeg", ".png").contains(extension)) {
            throw new BusinessException("仅支持JPG和PNG图片");
        }
        return extension;
    }
}
