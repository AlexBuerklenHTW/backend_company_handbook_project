package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public interface ArticleService {

    @Transactional
    Article createArticle(ArticleRequestDto articleDto);

    @Transactional(readOnly = true)
    Article getArticleById(String publicId);

    @Transactional(readOnly = true)
    List<Article> getArticlesByStatus();

    @Transactional(readOnly = true)
    List<Article> getApprovedArticles();

    @Transactional
    Article updateArticle(String id, ArticleRequestDto articleDto, Integer version);

    @Transactional
    Article setApprovalStatus(String id, String status, Integer version, String username);

    @Transactional(readOnly = true)
    List<Article> getArticlesByRoleAndStatus(String publicId, String role);

    @Transactional(readOnly = true)
    List<Article> getArticlesByUserAndStatus(String username, Article.ArticleStatus status);

    @Transactional
    Article getLatestArticleByPublicId(String publicId);

    @Transactional(readOnly = true)
    Article getApprovedArticleByPublicIdAndLastVersion(String publicId);

    @Transactional(readOnly = true)
    List<Article> getAllApprovedArticlesByPublicId(String publicId);

    @Transactional(readOnly = true)
    Article getArticleByPublicIdAndVersion(String publicId, Integer version);
}
