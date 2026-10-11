package org.example.aispringboot.DTO.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleStatusCommandDTO {
    @NotNull(message = "文章状态不能为空")
    @Min(value = 0, message = "文章状态不正确")
    @Max(value = 2, message = "文章状态不正确")
    private Integer status;
}
