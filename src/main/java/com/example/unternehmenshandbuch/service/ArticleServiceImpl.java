package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.persistence.ArticleRepository;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

	private final ArticleRepository repository;

	public ArticleServiceImpl(ArticleRepository repository) {
		this.repository = repository;
	}

	@Override
	public Article createArticle(ArticleRequestDto articleDto) {
		ArticleValidationException.validateArticleRequestDto(articleDto);

		Article article = Article.builder()
				.title(articleDto.getTitle())
				.description(articleDto.getDescription())
				.content(articleDto.getContent())
				.status(Article.ArticleStatus.EDITING)
				.editedBy(articleDto.getEditedBy())
				.build();

		return repository.save(article);
	}

	@Override
	public Article getArticleById(String id) {
		ArticleValidationException.validateId(id);
		return repository.findFirstByPublicId(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
	}

	@Override
	public List<Article> getArticlesByStatus() {
		return repository.findByStatus(Article.ArticleStatus.SUBMITTED);
	}

	@Override
	public List<Article> getApprovedArticles() {
		return repository.findByStatus(Article.ArticleStatus.APPROVED);
	}

	@Override
	public Article updateArticle(String id, ArticleRequestDto articleDto) {
		ArticleValidationException.validateId(id);
		ArticleValidationException.validateArticleRequestDto(articleDto);

		Article existingArticle = repository.findFirstByPublicId(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

		if (existingArticle.getStatus() == Article.ArticleStatus.EDITING ||
				existingArticle.getStatus() == Article.ArticleStatus.SUBMITTED) {
			existingArticle.setTitle(articleDto.getTitle());
			existingArticle.setDescription(articleDto.getDescription());
			existingArticle.setContent(articleDto.getContent());
			existingArticle.setStatus(articleDto.getStatus());
			existingArticle.setEditedBy(articleDto.getEditedBy());
			return repository.save(existingArticle);
		} else {

			int newVersion = repository.findFirstByPublicIdOrderByVersionDesc(id)
					.map(Article::getVersion)
					.orElse(0) + 1;

			Article newArticleVersion = Article.builder()
					.publicId(existingArticle.getPublicId())
					.title(articleDto.getTitle())
					.description(articleDto.getDescription())
					.content(articleDto.getContent())
					.version(newVersion)
					.status(Article.ArticleStatus.EDITING)
					.editedBy(articleDto.getEditedBy())
					.build();

			return repository.save(newArticleVersion);
		}
	}

	@Override
	public Article setApprovalStatus(String id, String status, Integer version, String username) {
		ArticleValidationException.validateId(id);
		ArticleValidationException.validateApprovalStatus(Article.ArticleStatus.valueOf(status));

		Article article = repository.findByPublicIdAndEditedByAndVersionNull(id, username)
				.orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id + " and version: " + version));

		article.setStatus(Article.ArticleStatus.valueOf(status.toUpperCase()));
		article.setVersion(version);

		return repository.save(article);
	}

	@Override
	public List<Article> getArticlesByRoleAndStatus(String publicId, String role) {
		ArticleValidationException.validateId(publicId);
		if (role.equals("ROLE_USER")) {
			return repository.findByPublicIdAndVersionNotNull(publicId);
		} else {
			return repository.findByPublicIdAndStatus(publicId, Article.ArticleStatus.SUBMITTED);
		}
	}

	@Override
	public List<Article> getArticlesByUserAndStatus(String username, Article.ArticleStatus status) {
		return repository.findByEditedByAndStatus(username, status);
	}

	@Override
	public Article getLatestArticleByPublicId(String publicId) {
		return repository.findFirstByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("No article found with publicId: " + publicId));
	}

	@Override
	public Article getLatestSubmittedArticleByPublicId(String publicId) {
		return repository.findFirstByPublicIdOrderByVersionDesc(publicId)
				.filter(article -> article.getStatus() == Article.ArticleStatus.SUBMITTED)
				.orElseThrow(() -> new ResourceNotFoundException("No submitted article found with publicId: " + publicId));
	}

	@Override
	public Article getLatestArticleByPublicIdWithVersion(String publicId, Integer version) {
		return repository.findByPublicIdAndVersion(publicId, version);
	}

}
