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

    @Transactional
    Article getArticleByPublicIdAndVersion(String publicId, Integer version);

    @Transactional(readOnly = true)
    List<Article> getArticlesByStatus();

    @Transactional(readOnly = true)
    List<Article> getApprovedArticles();

    @Transactional
    Article updateArticle(String id, ArticleRequestDto articleDto, Integer version, Boolean isEditable);

    @Transactional
    Article approveArticle(String publicId, ArticleRequestDto articleDto);

    @Transactional
    Article setSubmitStatus(ArticleRequestDto articleDto);

    @Transactional(readOnly = true)
    List<Article> getArticlesByUserAndStatus(String username, Article.ArticleStatus status);

    @Transactional
    Article getLatestArticleByPublicId(String publicId);

    @Transactional(readOnly = true)
    Article getApprovedArticleByPublicIdAndLastVersion(String publicId);

    @Transactional(readOnly = true)
    Article getSubmittedArticleByPublicIdAndStatus(String publicId,  Article.ArticleStatus status);

    @Transactional(readOnly = true)
    List<Article> getAllApprovedArticlesByPublicId(String publicId, Article.ArticleStatus status);

    @Transactional(readOnly = true)
    Article getArticleByPublicIdAndVersionAndStatus(String publicId, Integer version, Article.ArticleStatus status);

    Article declineArticleByPublicIdAndStatus(String publicId, Article.ArticleStatus status);

    @Transactional(readOnly = true)
    Article getEditedByWithStatusEditingAndVersion(String publicId);
}
