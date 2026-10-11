package org.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KnowledgeArticleCommandDTO {
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题不能超过200个字符")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String coverImage;

    @NotNull(message = "文章分类不能为空")
    private Long categoryId;

    private String summary;

    @Size(max = 500, message = "文章标签不能超过500个字符")
    private String tags;
}
