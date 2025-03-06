package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.persistence.ArticleRepository;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArticleServiceImplTest {

    @Mock
    private ArticleRepository repository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private ArticleRequestDto articleRequestDto;
    private Article article;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        articleRequestDto = ArticleRequestDto.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("user")
                .isEditable(true)
                .isSubmitted(false)
                .build();

        article = Article.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("user")
                .isEditable(true)
                .isSubmitted(false)
                .denyText("denied")
                .createdAt(Instant.parse("2024-03-06T12:34:56.789Z"))
                .build();
    }

    @Test
    public void testCreateArticle_Success() {
        when(repository.save(any(Article.class))).thenReturn(article);

        Article result = articleService.createArticle(articleRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        verify(repository, times(1)).save(any(Article.class));
    }

    @Test
    public void testCreateArticle_Invalid() {
        ArticleRequestDto invalidDto = ArticleRequestDto.builder()
                .title("")
                .description("Test Description")
                .content("Test Content")
                .status(Article.ArticleStatus.EDITING)
                .editedBy("user")
                .isSubmitted(false)
                .build();

        assertThatThrownBy(() -> articleService.createArticle(invalidDto))
                .isInstanceOf(ArticleValidationException.class);
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_Success() {
        when(repository.findByPublicIdAndVersion("test-id", 1)).thenReturn(Optional.of(article));

        Article result = articleService.getArticleByPublicIdAndVersion("test-id", 1);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        verify(repository, times(1)).findByPublicIdAndVersion("test-id", 1);
    }

    @Test
    public void testGetArticleByPublicIdAndVersionAndStatus_Success() {
        when(repository.findArticleByPublicIdAndVersionAndStatus("test-id", 1, Article.ArticleStatus.EDITING)).thenReturn(Optional.of(article));

        Article result = articleService.getArticleByPublicIdAndVersionAndStatus("test-id", 1, Article.ArticleStatus.EDITING);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        assertThat(result.getVersion()).isEqualTo(1);
        verify(repository, times(1)).findArticleByPublicIdAndVersionAndStatus("test-id", 1, Article.ArticleStatus.EDITING);
    }

    @Test
    public void testGetSubmittedArticleByPublicIdAndStatus_Success() {
        when(repository.findByPublicIdAndStatus("test-id",  Article.ArticleStatus.EDITING)).thenReturn(article);

        Article result = articleService.getSubmittedArticleByPublicIdAndStatus("test-id",  Article.ArticleStatus.EDITING);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        verify(repository, times(1)).findByPublicIdAndStatus("test-id",  Article.ArticleStatus.EDITING);
    }

    @Test
    public void testGetAllApprovedArticlesByPublicId_Success() {

        List<Article> articles = Collections.singletonList(article);

        when(repository.findAllApprovedArticlesByPublicId("test-id", Article.ArticleStatus.EDITING))
                .thenReturn(articles);

        List<Article> result = articleService.getAllApprovedArticlesByPublicId("test-id", Article.ArticleStatus.EDITING);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getPublicId()).isEqualTo("test-id");
        assertThat(result.get(0).getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        verify(repository, times(1)).findAllApprovedArticlesByPublicId("test-id", Article.ArticleStatus.EDITING);
    }


    @Test
    public void testGetArticleByPublicIdAndVersion_NotFound() {
        when(repository.findByPublicIdAndVersion("non-existent-id", 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticleByPublicIdAndVersion("non-existent-id", 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Article not found with publicId: non-existent-id");
    }

    @Test
    public void testGetArticlesByStatus_Success() {
        when(repository.findAllByStatus(Article.ArticleStatus.SUBMITTED)).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByStatus();

        assertThat(articles).isNotEmpty();
        verify(repository, times(1)).findAllByStatus(Article.ArticleStatus.SUBMITTED);
    }

    @Test
    public void testGetApprovedArticles_Success() {
        when(repository.findAllByStatus(Article.ArticleStatus.APPROVED)).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getApprovedArticles();

        assertThat(articles).isNotEmpty();
        verify(repository, times(1)).findAllByStatus(Article.ArticleStatus.APPROVED);
    }

    @Test
    public void testApproveArticle_Success() {
        article.setStatus(Article.ArticleStatus.SUBMITTED);
        article.setIsEditable(false);
        article.setIsSubmitted(true);

        when(repository.findByPublicIdAndStatus(article.getPublicId(), Article.ArticleStatus.SUBMITTED))
                .thenReturn(article);
        when(repository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article result = articleService.approveArticle(article.getPublicId(), articleRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(article.getPublicId());
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getTitle()).isEqualTo(articleRequestDto.getTitle());
        assertThat(result.getDescription()).isEqualTo(articleRequestDto.getDescription());
        assertThat(result.getContent()).isEqualTo(articleRequestDto.getContent());
        assertThat(result.getEditedBy()).isEqualTo(articleRequestDto.getEditedBy());
        assertThat(result.getIsEditable()).isTrue();
        assertThat(result.getIsSubmitted()).isFalse();

        verify(repository, times(1)).findByPublicIdAndStatus(article.getPublicId(), Article.ArticleStatus.SUBMITTED);
        verify(repository, times(2)).save(any(Article.class));
    }

    @Test
    public void testUpdateArticle_EditingStatus_Success() {
        Integer version = 2;
        Boolean isEditable = true;
        article.setStatus(Article.ArticleStatus.EDITING);

        when(repository.findByStatus(Article.ArticleStatus.EDITING)).thenReturn(article);
        when(repository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article result = articleService.updateArticle(article.getPublicId(), articleRequestDto, version, isEditable);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        assertThat(result.getVersion()).isEqualTo(version);
        assertThat(result.getIsEditable()).isEqualTo(isEditable);
        assertThat(result.getIsSubmitted()).isFalse();

        verify(repository, times(1)).findByStatus(Article.ArticleStatus.EDITING);
        verify(repository, times(1)).save(any(Article.class));
    }

    @Test
    public void testUpdateArticle_ApprovedStatus_CreatesNewArticle() {
        Integer version = 2;
        articleRequestDto.setStatus(Article.ArticleStatus.APPROVED);

        Article approvedArticle = Article.builder()
                .publicId(article.getPublicId())
                .status(Article.ArticleStatus.APPROVED)
                .isEditable(true)
                .build();

        when(repository.findByPublicIdAndStatusAndIsEditableTrue(article.getPublicId(), Article.ArticleStatus.APPROVED))
                .thenReturn(approvedArticle);
        when(repository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article result = articleService.updateArticle(article.getPublicId(), articleRequestDto, version, false);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(article.getPublicId());
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        assertThat(result.getVersion()).isEqualTo(version);
        assertThat(result.getIsEditable()).isFalse();
        assertThat(result.getIsSubmitted()).isFalse();

        verify(repository, times(1)).findByPublicIdAndStatusAndIsEditableTrue(article.getPublicId(), Article.ArticleStatus.APPROVED);
        verify(repository, times(1)).save(any(Article.class));
    }

    @Test
    public void testUpdateArticle_OtherStatus_UpdatesExistingArticle() {
        Integer version = 2;
        Boolean isEditable = false;
        articleRequestDto.setStatus(Article.ArticleStatus.SUBMITTED);

        when(repository.findByPublicIdAndVersion(article.getPublicId(), version))
                .thenReturn(Optional.of(article));
        when(repository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article result = articleService.updateArticle(article.getPublicId(), articleRequestDto, version, isEditable);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.SUBMITTED);
        assertThat(result.getVersion()).isEqualTo(version);
        assertThat(result.getIsEditable()).isEqualTo(isEditable);
        assertThat(result.getIsSubmitted()).isTrue();

        verify(repository, times(1)).findByPublicIdAndVersion(article.getPublicId(), version);
        verify(repository, times(1)).save(any(Article.class));
    }



    @Test
    public void testSetSubmitStatus_Success() {
        when(repository.findByPublicIdAndStatus("test-id", Article.ArticleStatus.EDITING)).thenReturn(article);
        when(repository.save(any(Article.class))).thenReturn(article);

        Article result = articleService.setSubmitStatus(articleRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.SUBMITTED);
        verify(repository, times(1)).save(any(Article.class));
    }

    @Test
    public void testGetArticlesByUserAndStatus_Success() {
        when(repository.findByEditedByAndStatus("user", Article.ArticleStatus.EDITING)).thenReturn(Collections.singletonList(article));

        List<Article> result = articleService.getArticlesByUserAndStatus("user", Article.ArticleStatus.EDITING);

        assertThat(result).isNotEmpty();
        verify(repository, times(1)).findByEditedByAndStatus("user", Article.ArticleStatus.EDITING);
    }

    @Test
    public void testGetLatestArticleByPublicId_Success() {
        when(repository.findFirstByPublicId("test-id")).thenReturn(Optional.of(article));

        Article result = articleService.getLatestArticleByPublicId("test-id");

        assertThat(result).isNotNull();
        verify(repository, times(1)).findFirstByPublicId("test-id");
    }

    @Test
    public void testGetApprovedArticleByPublicIdAndLastVersion_Success() {
        when(repository.findLatestApprovedArticleByPublicId("test-id")).thenReturn(Optional.of(article));

        Article result = articleService.getApprovedArticleByPublicIdAndLastVersion("test-id");

        assertThat(result).isNotNull();
        verify(repository, times(1)).findLatestApprovedArticleByPublicId("test-id");
    }

    @Test
    public void testDeclineArticleByPublicIdAndStatus_Success() {
        when(repository.findByPublicIdAndStatus("test-id", Article.ArticleStatus.SUBMITTED)).thenReturn(article);
        when(repository.save(any(Article.class))).thenReturn(article);

        Article result = articleService.declineArticleByPublicIdAndStatus("test-id", Article.ArticleStatus.SUBMITTED, "Reason");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
        verify(repository, times(1)).save(any(Article.class));
    }

    @Test
    public void testGetEditedByWithStatusEditingAndVersion_Success() {
        when(repository.getEditedByWithStatusEditingAndVersion("test-id")).thenReturn(article);

        Article result = articleService.getEditedByWithStatusEditingAndVersion("test-id");

        assertThat(result).isNotNull();
        verify(repository, times(1)).getEditedByWithStatusEditingAndVersion("test-id");
    }
}
