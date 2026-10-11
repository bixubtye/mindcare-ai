package org.example.aispringboot.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeArticleResponseDTO {
    private String id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String tags;
    private Long authorId;
    private String authorName;
    private Integer readCount;
    private Integer status;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
