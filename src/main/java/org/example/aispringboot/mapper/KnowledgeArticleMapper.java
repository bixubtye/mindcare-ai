package org.example.aispringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.aispringboot.DTO.response.KnowledgeArticleResponseDTO;
import org.example.aispringboot.entity.KnowledgeArticle;

import java.util.List;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
    String ARTICLE_FILTER = """
            FROM knowledge_article a
            LEFT JOIN knowledge_category c ON c.id = a.category_id
            LEFT JOIN user u ON u.id = a.author_id
            WHERE 1 = 1
            <if test="title != null and title != ''">
                AND a.title LIKE CONCAT('%', #{title}, '%')
            </if>
            <if test="categoryId != null">
                AND a.category_id = #{categoryId}
            </if>
            <if test="status != null">
                AND a.status = #{status}
            </if>
            <if test="authorName != null and authorName != ''">
                AND (u.username LIKE CONCAT('%', #{authorName}, '%')
                    OR u.nickname LIKE CONCAT('%', #{authorName}, '%'))
            </if>
            <if test="publishedOnly">
                AND a.status = 1
            </if>
            """;

    @Select("""
            <script>
            SELECT a.id, a.category_id, c.category_name, a.title, a.summary, a.content,
                   a.cover_image, a.tags, a.author_id,
                   COALESCE(NULLIF(u.nickname, ''), u.username) AS author_name,
                   a.read_count, a.status, a.published_at, a.created_at, a.updated_at
            """ + ARTICLE_FILTER + """
            ORDER BY
            <choose>
                <when test="sortField == 'readCount'">a.read_count</when>
                <when test="sortField == 'publishedAt'">a.published_at</when>
                <when test="sortField == 'updatedAt'">a.updated_at</when>
                <otherwise>a.created_at</otherwise>
            </choose>
            <choose>
                <when test="sortAscending">ASC</when>
                <otherwise>DESC</otherwise>
            </choose>
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<KnowledgeArticleResponseDTO> selectArticlePage(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("authorName") String authorName,
            @Param("publishedOnly") boolean publishedOnly,
            @Param("sortField") String sortField,
            @Param("sortAscending") boolean sortAscending,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
            <script>
            SELECT COUNT(*)
            """ + ARTICLE_FILTER + """
            </script>
            """)
    Long countArticles(@Param("title") String title,
                       @Param("categoryId") Long categoryId,
                       @Param("status") Integer status,
                       @Param("authorName") String authorName,
                       @Param("publishedOnly") boolean publishedOnly);

    @Select("""
            SELECT a.id, a.category_id, c.category_name, a.title, a.summary, a.content,
                   a.cover_image, a.tags, a.author_id,
                   COALESCE(NULLIF(u.nickname, ''), u.username) AS author_name,
                   a.read_count, a.status, a.published_at, a.created_at, a.updated_at
            FROM knowledge_article a
            LEFT JOIN knowledge_category c ON c.id = a.category_id
            LEFT JOIN user u ON u.id = a.author_id
            WHERE a.id = #{id}
            """)
    KnowledgeArticleResponseDTO selectArticleDetail(@Param("id") String id);

    @Update("UPDATE knowledge_article SET read_count = read_count + 1 WHERE id = #{id}")
    int increaseReadCount(@Param("id") String id);

    @Update("""
            UPDATE knowledge_article
            SET status = #{status},
                published_at = CASE WHEN #{status} = 1 AND published_at IS NULL THEN NOW() ELSE published_at END,
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateArticleStatus(@Param("id") String id, @Param("status") Integer status);
}
