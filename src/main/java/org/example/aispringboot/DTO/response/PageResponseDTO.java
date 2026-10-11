package org.example.aispringboot.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
}
