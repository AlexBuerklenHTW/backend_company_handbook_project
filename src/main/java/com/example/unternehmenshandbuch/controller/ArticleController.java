package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.service.dto.ApprovalRequestDto;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import com.example.unternehmenshandbuch.mapper.ArticleMapper;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ArticleController implements ArticleResource {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleController(ArticleService articleService, ArticleMapper articleMapper) {
        this.articleService = articleService;
        this.articleMapper = articleMapper;
    }

	@Override
	public ResponseEntity<ArticleResponseDto> createArticle(ArticleRequestDto articleRequestDto) {
		Article createdArticle = articleService.createArticle(articleRequestDto);
		return ResponseEntity.ok(articleMapper.mapToDto(createdArticle));
	}

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusSubmitted() {
        List<Article> articles = articleService.getArticlesByStatus();
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesApproved() {
        List<Article> articles = articleService.getApprovedArticles();
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getLatestArticleByPublicIdAndStatusEditedBy(String publicId) {
        Article article = articleService.getLatestArticleByPublicId(publicId);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> updateArticle(String id, ArticleRequestDto articleRequestDto, Integer version, Boolean isEditable) {
        Article updatedArticle = articleService.updateArticle(id, articleRequestDto, version, isEditable);
        return ResponseEntity.ok(articleMapper.mapToDto(updatedArticle));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> setApprovalStatus(String id, ApprovalRequestDto approvalRequest) {
        Article updatedArticle = articleService.setApprovalStatus(id, approvalRequest.getStatus(), approvalRequest.getVersion(), approvalRequest.getEditedBy());
        return ResponseEntity.ok(articleMapper.mapToDto(updatedArticle));
    }

//    @Override
//    public ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusAndRole(String publicId, String role) {
//        List<Article> articles = articleService.getArticlesByRoleAndStatus(publicId, role);
//        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
//    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesEditedByUser(String username) {
        List<Article> articles = articleService.getArticlesByUserAndStatus(username, Article.ArticleStatus.EDITING);
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getApprovedArticleByPublicIdAndLastVersion(String publicId) {
        Article article = articleService.getApprovedArticleByPublicIdAndLastVersion(publicId);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getSubmittedArticleByPublicIdAndStatus(String publicId, String status) {
        Article.ArticleStatus statusInEnum = Article.ArticleStatus.valueOf(status.toUpperCase());
        Article article = articleService.getSubmittedArticleByPublicIdAndStatus(publicId, statusInEnum);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getAllApprovedArticlesByPublicId(String publicId) {
        List<Article> articles = articleService.getAllApprovedArticlesByPublicId(publicId);
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersionAndStatus(String publicId, Integer version, String status) {
        Article.ArticleStatus statusInEnum = Article.ArticleStatus.valueOf(status.toUpperCase());
        Article article = articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, statusInEnum);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersion(String publicId, Integer version) {
        Article article = articleService.getArticleByPublicIdAndVersion(publicId, version);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }
}
