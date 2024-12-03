package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.helper.Helper;
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

        String publicId = Helper.generateOrRetrievePublicId(articleDto.getPublicId());

        Integer version = Helper.generateOrRetrieveVersion(articleDto.getVersion());

        Article article = Article.builder()
                .publicId(publicId)
                .title(articleDto.getTitle())
                .description(articleDto.getDescription())
                .content(articleDto.getContent())
                .status(Article.ArticleStatus.EDITING)
                .editedBy(articleDto.getEditedBy())
                .isEditable(false)
                .version(version)
                .editedBy(articleDto.getEditedBy())
                .isSubmitted(false)
                .build();

        ArticleValidationException.validateArticle(article);

        return repository.save(article);
    }

    @Override
    public Article getArticleByPublicIdAndVersion(String publicId, Integer version) {
        return repository.findByPublicIdAndVersion(publicId, version)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with publicId: " + publicId));
    }

    @Override
    public List<Article> getArticlesByStatus() {
        return repository.findAllByStatus(Article.ArticleStatus.SUBMITTED);
    }

    @Override
    public List<Article> getApprovedArticles() {
        return repository.findAllByStatus(Article.ArticleStatus.APPROVED);
    }

    @Override
    public Article updateArticle(String publicId, ArticleRequestDto articleDto, Integer version, Boolean isEditable) {

        ArticleValidationException.validateId(publicId);
        ArticleValidationException.validateArticleRequestDto(articleDto);

        if (articleDto.getStatus() == Article.ArticleStatus.APPROVED) {
            Article article = Article.builder()
                    .id(null)
                    .publicId(publicId)
                    .title(articleDto.getTitle())
                    .description(articleDto.getDescription())
                    .content(articleDto.getContent())
                    .status(Article.ArticleStatus.EDITING)
                    .editedBy(articleDto.getEditedBy())
                    .isEditable(false)
                    .version(version)
                    .editedBy(articleDto.getEditedBy())
                    .isSubmitted(false)
                    .build();

            Article articleWithStatusApproved = repository.findByPublicIdAndStatusAndIsEditableTrue(publicId, Article.ArticleStatus.APPROVED);
            System.out.println(articleWithStatusApproved);
            articleWithStatusApproved.setIsEditable(false);

            return repository.save(article);

        } else if (articleDto.getStatus() == Article.ArticleStatus.EDITING) {

            Article existingArticle = repository.findByStatus(articleDto.getStatus());
            existingArticle.setTitle(articleDto.getTitle());
            existingArticle.setDescription(articleDto.getDescription());
            existingArticle.setContent(articleDto.getContent());
            existingArticle.setStatus(Article.ArticleStatus.EDITING);
            existingArticle.setEditedBy(articleDto.getEditedBy());
            existingArticle.setVersion(version);
            existingArticle.setIsEditable(isEditable);
            existingArticle.setIsSubmitted(false);

            return repository.save(existingArticle);
        } else {

            Article existingArticle = repository.findByPublicIdAndVersion(publicId, version)
                    .orElseThrow(() -> new ResourceNotFoundException("Article not found with PublicId: " + publicId));

            existingArticle.setTitle(articleDto.getTitle());
            existingArticle.setDescription(articleDto.getDescription());
            existingArticle.setContent(articleDto.getContent());
            existingArticle.setStatus(articleDto.getStatus());
            existingArticle.setEditedBy(articleDto.getEditedBy());
            existingArticle.setVersion(version);
            existingArticle.setIsEditable(isEditable);
            existingArticle.setIsSubmitted(true);
            return repository.save(existingArticle);

        }
    }

    @Override
    public Article approveArticle(String publicId, ArticleRequestDto articleRequestDto) {
        ArticleValidationException.validateArticleRequestDto(articleRequestDto);

        Article existingArticle = repository.findByPublicIdAndStatus(publicId, Article.ArticleStatus.SUBMITTED);

        Integer newVersion = articleRequestDto.getVersion() + 1;

        existingArticle.setVersion(newVersion);
        existingArticle.setStatus(Article.ArticleStatus.APPROVED);
        existingArticle.setTitle(articleRequestDto.getTitle());
        existingArticle.setDescription(articleRequestDto.getDescription());
        existingArticle.setContent(articleRequestDto.getContent());
        existingArticle.setEditedBy(articleRequestDto.getEditedBy());
        existingArticle.setIsEditable(true);
        existingArticle.setIsSubmitted(false);
        repository.save(existingArticle);

        return repository.save(existingArticle);
    }

    @Override
    public Article setSubmitStatus(ArticleRequestDto articleDto) {

        Article existingArticle = repository.findByPublicIdAndStatus(articleDto.getPublicId(), Article.ArticleStatus.EDITING);

        existingArticle.setTitle(articleDto.getTitle());
        existingArticle.setDescription(articleDto.getDescription());
        existingArticle.setContent(articleDto.getContent());
        existingArticle.setStatus(Article.ArticleStatus.SUBMITTED);
        existingArticle.setEditedBy(articleDto.getEditedBy());
        existingArticle.setVersion(articleDto.getVersion());
        existingArticle.setIsEditable(false);
        existingArticle.setIsSubmitted(true);

        ArticleValidationException.validateArticle(existingArticle);

        return repository.save(existingArticle);
    }

    @Override
    public List<Article> getArticlesByUserAndStatus(String username, Article.ArticleStatus status) {
        return repository.findByEditedByAndStatus(username, status);
    }

    @Override
    public Article getLatestArticleByPublicId(String publicId) {
        ArticleValidationException.validateId(publicId);
        return repository.findFirstByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("No article found with publicId: " + publicId));
    }

    @Override
    public Article getApprovedArticleByPublicIdAndLastVersion(String publicId) {
        ArticleValidationException.validateId(publicId);
        return repository.findLatestApprovedArticleByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("No approved article found with publicId: " + publicId));
    }

    @Override
    public Article getSubmittedArticleByPublicIdAndStatus(String publicId, Article.ArticleStatus status) {
        ArticleValidationException.validateId(publicId);
        return repository.findByPublicIdAndStatus(publicId, status);
    }

    @Override
    public List<Article> getAllApprovedArticlesByPublicId(String publicId, Article.ArticleStatus status) {
        ArticleValidationException.validateId(publicId);
        return repository.findAllApprovedArticlesByPublicId(publicId, status);
    }

    @Override
    public Article getArticleByPublicIdAndVersionAndStatus(String publicId, Integer version, Article.ArticleStatus status) {
        ArticleValidationException.validateId(publicId);
        return repository.findArticleByPublicIdAndVersionAndStatus(publicId, version, status)
                .orElseThrow(() -> new ResourceNotFoundException("No article found with publicId: " + publicId));
    }

    @Override
    public Article declineArticleByPublicIdAndStatus(String publicId, Article.ArticleStatus status) {
        ArticleValidationException.validateId(publicId);
        Article articleWithStatusSubmitted = repository.findByPublicIdAndStatus(publicId, status);

        System.out.println("articleWithStatusSubmitted: " + articleWithStatusSubmitted);

        articleWithStatusSubmitted.setStatus(Article.ArticleStatus.EDITING);
        articleWithStatusSubmitted.setIsSubmitted(false);

        return repository.save(articleWithStatusSubmitted);
    }
}
