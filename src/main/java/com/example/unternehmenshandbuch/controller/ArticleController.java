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
	public ResponseEntity<ArticleResponseDto> createArticle(@RequestBody ArticleRequestDto articleRequestDto) {
		Article createdArticle = articleService.createArticle(articleRequestDto);
		return ResponseEntity.ok(articleMapper.mapToDto(createdArticle));
	}

    @Override
    public ResponseEntity<ArticleResponseDto> getArticleById(String publicId) {
        Article article = articleService.getArticleById(publicId);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusSubmitted() {
        List<Article> articles = articleService.getArticlesByStatus();
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getApprovedArticles() {
        List<Article> articles = articleService.getApprovedArticles();
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> getLatestArticleByPublicId(String publicId) {
        Article article = articleService.getLatestArticleByPublicId(publicId);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> updateArticle(String id, ArticleRequestDto articleRequestDto) {
        Article updatedArticle = articleService.updateArticle(id, articleRequestDto);
        return ResponseEntity.ok(articleMapper.mapToDto(updatedArticle));
    }

    @Override
    public ResponseEntity<ArticleResponseDto> setApprovalStatus(String id, ApprovalRequestDto approvalRequest) {
        Article updatedArticle = articleService.setApprovalStatus(id, approvalRequest.getStatus(), approvalRequest.getVersion(), approvalRequest.getEditedBy());
        return ResponseEntity.ok(articleMapper.mapToDto(updatedArticle));
    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusAndRole(String publicId, String role) {
        List<Article> articles = articleService.getArticlesByRoleAndStatus(publicId, role);
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

//    @Override
//    public ResponseEntity<ArticleResponseDto> getLatestArticleByPublicId(String publicId) {
//        Article article = articleService.getLatestArticleByPublicId(publicId);
//        return ResponseEntity.ok(articleMapper.mapToDto(article));
//    }

    @Override
    public ResponseEntity<List<ArticleResponseDto>> getArticlesEditedByUser(String username) {
        List<Article> articles = articleService.getArticlesByUserAndStatus(username, Article.ArticleStatus.EDITING);
        return ResponseEntity.ok(articleMapper.mapToDtoList(articles));
    }

    //
    public ResponseEntity<ArticleResponseDto> getLatestSubmittedArticleByPublicId(String publicId) {
        Article article = articleService.getLatestSubmittedArticleByPublicId(publicId);
        return ResponseEntity.ok(articleMapper.mapToDto(article));
    }

}
