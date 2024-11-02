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
        return repository.findByStatus(Article.ArticleStatus.SUBMITTED);
    }

    @Override
    public List<Article> getApprovedArticles() {
        return repository.findByStatus(Article.ArticleStatus.APPROVED);
    }

    @Override
    public Article updateArticle(String publicId, ArticleRequestDto articleDto, Integer version, Boolean isEditable) {

        System.out.println("updateArticle wird aufgerufen");
        System.out.println("status: " + articleDto.getStatus());
        ArticleValidationException.validateId(publicId);
        ArticleValidationException.validateArticleRequestDto(articleDto);

        if (articleDto.getStatus() == Article.ArticleStatus.APPROVED) {
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
            return repository.save(article);
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

        repository.updateStatusByPublicId(Article.ArticleStatus.SUBMITTED, Article.ArticleStatus.DECLINED, publicId);
        repository.updateStatusByPublicId(Article.ArticleStatus.EDITING, Article.ArticleStatus.DECLINED, publicId);


        Integer newVersion = articleRequestDto.getVersion() + 1;

        Article article = Article.builder()
                .publicId(publicId)
                .title(articleRequestDto.getTitle())
                .description(articleRequestDto.getDescription())
                .content(articleRequestDto.getContent())
                .status(Article.ArticleStatus.APPROVED)
                .editedBy(articleRequestDto.getEditedBy())
                .isEditable(true)
                .isSubmitted(false)
                .version(newVersion)
                .build();

        return repository.save(article);
    }

    @Override
    public Article setEditingStatus(String publicId, ArticleRequestDto articleDto) {

        System.out.println("setEditingStatus: " + articleDto.getVersion());

//		Integer version = (articleDto.getVersion() != null && articleDto.getVersion() != 0) ? articleDto.getVersion() : null;

        Article article = Article.builder()
                .publicId(articleDto.getPublicId())
                .title(articleDto.getTitle())
                .description(articleDto.getDescription())
                .content(articleDto.getContent())
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy(articleDto.getEditedBy())
                .isEditable(false)
                .isSubmitted(true)
                .version(articleDto.getVersion())
                .build();

        System.out.println("setEditingStatus article: " + article);

        ArticleValidationException.validateArticle(article);

        return repository.save(article);
    }

//	@Override
//	public List<Article> getArticlesByRoleAndStatus(String publicId, String role) {
//		ArticleValidationException.validateId(publicId);
//		if (role.equals("ROLE_USER")) {
//			return repository.findByPublicIdAndVersionNotNull(publicId);
//		} else {
//			return repository.findByPublicIdAndStatus(publicId, Article.ArticleStatus.SUBMITTED);
//		}
//	}

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
    public List<Article> getAllApprovedArticlesByPublicId(String publicId) {
        ArticleValidationException.validateId(publicId);
        return repository.findAllApprovedArticlesByPublicId(publicId);
    }

    @Override
    public Article getArticleByPublicIdAndVersionAndStatus(String publicId, Integer version, Article.ArticleStatus status) {
        ArticleValidationException.validateId(publicId);
        return repository.findArticleByPublicIdAndVersionAndStatus(publicId, version, status)
                .orElseThrow(() -> new ResourceNotFoundException("No article found with publicId: " + publicId));
    }
}
