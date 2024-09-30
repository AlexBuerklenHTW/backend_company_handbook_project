package com.example.unternehmenshandbuch.persistence;

import com.example.unternehmenshandbuch.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findFirstByPublicId(String publicId);

    List<Article> findByPublicIdAndVersionNotNull(String publicId);

    List<Article> findByStatus(Article.ArticleStatus status);

    Optional<Article> findFirstByPublicIdOrderByVersionDesc(String publicId);

    Optional<Article> findByPublicIdAndEditedByAndVersionNull(String publicId, String username);

    List<Article> findByEditedByAndStatus(String editedBy, Article.ArticleStatus status);

    List<Article> findByPublicIdAndStatus(String publicId, Article.ArticleStatus articleStatus);

}