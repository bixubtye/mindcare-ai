package org.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.command.KnowledgeArticleCommandDTO;
import org.example.aispringboot.DTO.response.KnowledgeArticleResponseDTO;
import org.example.aispringboot.DTO.response.KnowledgeCategoryResponseDTO;
import org.example.aispringboot.DTO.response.PageResponseDTO;
import org.example.aispringboot.entity.KnowledgeArticle;
import org.example.aispringboot.entity.KnowledgeCategory;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.KnowledgeArticleMapper;
import org.example.aispringboot.mapper.KnowledgeCategoryMapper;
import org.example.aispringboot.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KnowledgeService {
    @Resource
    private KnowledgeCategoryMapper knowledgeCategoryMapper;

    @Resource
    private KnowledgeArticleMapper knowledgeArticleMapper;

    public List<KnowledgeCategoryResponseDTO> getCategoryTree() {
        List<KnowledgeCategory> categories = knowledgeCategoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getStatus, 1)
                        .orderByAsc(KnowledgeCategory::getSortOrder)
                        .orderByAsc(KnowledgeCategory::getId));
        Map<Long, KnowledgeCategoryResponseDTO> categoryMap = new HashMap<>();
        for (KnowledgeCategory category : categories) {
            KnowledgeCategoryResponseDTO item = toCategoryResponse(category);
            categoryMap.put(item.getId(), item);
        }

        List<KnowledgeCategoryResponseDTO> roots = new ArrayList<>();
        for (KnowledgeCategoryResponseDTO category : categoryMap.values()) {
            KnowledgeCategoryResponseDTO parent = categoryMap.get(category.getParentId());
            if (parent == null || category.getParentId() == null || category.getParentId() == 0) {
                roots.add(category);
            } else {
                parent.getChildren().add(category);
            }
        }
        sortCategories(roots);
        return roots;
    }

    public PageResponseDTO<KnowledgeArticleResponseDTO> getArticlePage(
            String title, Long categoryId, Long category, Integer status, String authorName,
            Long currentPage, Long size, String sortField, String sortDirection) {
        long pageNumber = normalizePage(currentPage);
        long pageSize = normalizeSize(size);
        Long selectedCategoryId = categoryId != null ? categoryId : category;
        boolean publishedOnly = !CurrentUserUtil.isAdmin();
        Integer selectedStatus = publishedOnly ? null : status;
        String selectedSortField = normalizeSortField(sortField);
        boolean sortAscending = "asc".equalsIgnoreCase(sortDirection);

        List<KnowledgeArticleResponseDTO> records = knowledgeArticleMapper.selectArticlePage(
                trimToNull(title), selectedCategoryId, selectedStatus, trimToNull(authorName),
                publishedOnly, selectedSortField, sortAscending, (pageNumber - 1) * pageSize, pageSize);
        Long total = knowledgeArticleMapper.countArticles(
                trimToNull(title), selectedCategoryId, selectedStatus, trimToNull(authorName), publishedOnly);
        return new PageResponseDTO<>(records, total, pageNumber, pageSize);
    }

    public KnowledgeArticleResponseDTO getArticleDetail(String id) {
        KnowledgeArticleResponseDTO article = knowledgeArticleMapper.selectArticleDetail(id);
        if (article == null || (!CurrentUserUtil.isAdmin() && !Integer.valueOf(1).equals(article.getStatus()))) {
            throw new BusinessException("文章不存在或已下线");
        }
        if (!CurrentUserUtil.isAdmin()) {
            knowledgeArticleMapper.increaseReadCount(id);
            article.setReadCount(article.getReadCount() == null ? 1 : article.getReadCount() + 1);
        }
        return article;
    }

    @Transactional
    public KnowledgeArticleResponseDTO createArticle(KnowledgeArticleCommandDTO commandDTO) {
        validateCategory(commandDTO.getCategoryId());
        LocalDateTime now = LocalDateTime.now();
        String id = UUID.randomUUID().toString();
        KnowledgeArticle article = KnowledgeArticle.builder()
                .id(id)
                .categoryId(commandDTO.getCategoryId())
                .title(commandDTO.getTitle().trim())
                .summary(commandDTO.getSummary())
                .content(commandDTO.getContent())
                .coverImage(commandDTO.getCoverImage())
                .tags(commandDTO.getTags())
                .authorId(CurrentUserUtil.getUserId())
                .readCount(0)
                .status(1)
                .publishedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        knowledgeArticleMapper.insert(article);
        return knowledgeArticleMapper.selectArticleDetail(id);
    }

    @Transactional
    public KnowledgeArticleResponseDTO updateArticle(String id, KnowledgeArticleCommandDTO commandDTO) {
        if (knowledgeArticleMapper.selectById(id) == null) {
            throw new BusinessException("文章不存在");
        }
        validateCategory(commandDTO.getCategoryId());
        KnowledgeArticle article = KnowledgeArticle.builder()
                .id(id)
                .categoryId(commandDTO.getCategoryId())
                .title(commandDTO.getTitle().trim())
                .summary(commandDTO.getSummary())
                .content(commandDTO.getContent())
                .coverImage(commandDTO.getCoverImage())
                .tags(commandDTO.getTags())
                .updatedAt(LocalDateTime.now())
                .build();
        knowledgeArticleMapper.updateById(article);
        return knowledgeArticleMapper.selectArticleDetail(id);
    }

    public void updateArticleStatus(String id, Integer status) {
        if (knowledgeArticleMapper.updateArticleStatus(id, status) == 0) {
            throw new BusinessException("文章不存在");
        }
    }

    public void deleteArticle(String id) {
        if (knowledgeArticleMapper.deleteById(id) == 0) {
            throw new BusinessException("文章不存在");
        }
    }

    private void validateCategory(Long categoryId) {
        KnowledgeCategory category = knowledgeCategoryMapper.selectById(categoryId);
        if (category == null || !Integer.valueOf(1).equals(category.getStatus())) {
            throw new BusinessException("文章分类不存在或已禁用");
        }
    }

    private KnowledgeCategoryResponseDTO toCategoryResponse(KnowledgeCategory category) {
        KnowledgeCategoryResponseDTO response = new KnowledgeCategoryResponseDTO();
        response.setId(category.getId());
        response.setParentId(category.getParentId());
        response.setCategoryName(category.getCategoryName());
        response.setCategoryCode(category.getCategoryCode());
        response.setDescription(category.getDescription());
        response.setSortOrder(category.getSortOrder());
        response.setStatus(category.getStatus());
        return response;
    }

    private void sortCategories(List<KnowledgeCategoryResponseDTO> categories) {
        categories.sort(Comparator.comparing(
                KnowledgeCategoryResponseDTO::getSortOrder,
                Comparator.nullsLast(Integer::compareTo)));
        categories.forEach(category -> sortCategories(category.getChildren()));
    }

    private long normalizePage(Long currentPage) {
        return currentPage == null || currentPage < 1 ? 1 : currentPage;
    }

    private long normalizeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }

    private String normalizeSortField(String sortField) {
        return switch (sortField == null ? "" : sortField) {
            case "readCount", "publishedAt", "updatedAt" -> sortField;
            default -> "createdAt";
        };
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
