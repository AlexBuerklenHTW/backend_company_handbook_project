package com.example.unternehmenshandbuch.persistence;

import com.example.unternehmenshandbuch.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findFirstByPublicId(String publicId);

    Optional<Article> findFirstByPublicIdAndVersion(String publicId, Integer version);

    List<Article> findByPublicIdAndVersionNotNull(String publicId);

    List<Article> findByStatus(Article.ArticleStatus status);

    Optional<Article> findFirstByPublicIdOrderByVersionDesc(String publicId);

    Optional<Article> findByPublicIdAndEditedByAndVersionNull(String publicId, String username);

    List<Article> findByEditedByAndStatus(String editedBy, Article.ArticleStatus status);

    List<Article> findByPublicIdAndStatus(String publicId, Article.ArticleStatus articleStatus);

    @Query("SELECT a FROM Article a WHERE a.publicId = :publicId AND a.status = 'APPROVED' AND a.version = (SELECT MAX(a2.version) FROM Article a2 WHERE a2.publicId = :publicId AND a2.status = 'APPROVED')")
    Optional<Article> findLatestApprovedArticleByPublicId(@Param("publicId") String publicId);

    @Query("SELECT a FROM Article a WHERE a.publicId = :publicId AND a.status = 'APPROVED' ORDER BY a.version DESC")
    List<Article> findAllApprovedArticlesByPublicId(@Param("publicId") String publicId);

    Optional<Article> findArticleByPublicIdAndVersionAndStatus(String publicId, Integer version, Article.ArticleStatus status);
}