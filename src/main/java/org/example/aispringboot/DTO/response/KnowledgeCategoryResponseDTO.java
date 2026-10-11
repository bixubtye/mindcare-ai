package org.example.aispringboot.DTO.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KnowledgeCategoryResponseDTO {
    private Long id;
    private Long parentId;
    private String categoryName;
    private String categoryCode;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private List<KnowledgeCategoryResponseDTO> children = new ArrayList<>();
}
