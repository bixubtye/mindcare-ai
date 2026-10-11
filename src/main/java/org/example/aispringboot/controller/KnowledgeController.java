package org.example.aispringboot.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aispringboot.DTO.command.ArticleStatusCommandDTO;
import org.example.aispringboot.DTO.command.KnowledgeArticleCommandDTO;
import org.example.aispringboot.DTO.response.KnowledgeArticleResponseDTO;
import org.example.aispringboot.DTO.response.KnowledgeCategoryResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.KnowledgeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    @Resource
    private KnowledgeService knowledgeService;

    @GetMapping("/category/tree")
    public Result<List<KnowledgeCategoryResponseDTO>> getCategoryTree() {
        return Result.ok(knowledgeService.getCategoryTree());
    }

    @GetMapping("/article/page")
    public Result<PageResponseDTO<KnowledgeArticleResponseDTO>> getArticlePage(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) Long currentPage,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {
        return Result.ok(knowledgeService.getArticlePage(
                title, categoryId, category, status, authorName, currentPage, size, sortField, sortDirection));
    }

    @GetMapping("/article/{id}")
    public Result<KnowledgeArticleResponseDTO> getArticleDetail(@PathVariable String id) {
        return Result.ok(knowledgeService.getArticleDetail(id));
    }

    @PostMapping("/article")
    @PreAuthorize("hasRole('2')")
    public Result<KnowledgeArticleResponseDTO> createArticle(
            @Valid @RequestBody KnowledgeArticleCommandDTO commandDTO) {
        return Result.ok(knowledgeService.createArticle(commandDTO));
    }

    @PutMapping("/article/{id}")
    @PreAuthorize("hasRole('2')")
    public Result<KnowledgeArticleResponseDTO> updateArticle(
            @PathVariable String id, @Valid @RequestBody KnowledgeArticleCommandDTO commandDTO) {
        return Result.ok(knowledgeService.updateArticle(id, commandDTO));
    }

    @PutMapping("/article/{id}/status")
    @PreAuthorize("hasRole('2')")
    public Result<Void> updateArticleStatus(
            @PathVariable String id, @Valid @RequestBody ArticleStatusCommandDTO commandDTO) {
        knowledgeService.updateArticleStatus(id, commandDTO.getStatus());
        return Result.ok();
    }

    @DeleteMapping("/article/{id}")
    @PreAuthorize("hasRole('2')")
    public Result<Void> deleteArticle(@PathVariable String id) {
        knowledgeService.deleteArticle(id);
        return Result.ok();
    }
}
