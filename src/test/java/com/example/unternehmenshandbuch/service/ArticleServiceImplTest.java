package com.example.unternehmenshandbuch.service;

import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.persistence.ArticleRepository;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class ArticleServiceImplTest {

    @Mock
    private ArticleRepository repository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Captor
    private ArgumentCaptor<Article> articleCaptor;

    private Article article;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        article = Article.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(null)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("user")
                .build();
    }

    @Test
    public void testGetLatestArticleByPublicId_NotFound() {
        when(repository.findFirstByPublicIdOrderByVersionDesc(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getLatestArticleByPublicId("non-existent-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No article found with publicId: non-existent-id");
    }

    @Test
    public void testGetArticlesByUserAndStatus_Success() {
        when(repository.findByEditedByAndStatus(anyString(), any(Article.ArticleStatus.class))).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByUserAndStatus("user", Article.ArticleStatus.EDITING);

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);
    }

    @Test
    public void testGetApprovedArticleByPublicIdAndLastVersion_Success() {
        Article approvedArticle = Article.builder()
                .publicId("test-id")
                .title("Approved Title")
                .description("Approved Description")
                .content("Approved Content")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        when(repository.findLatestApprovedArticleByPublicId("test-id")).thenReturn(Optional.of(approvedArticle));

        Article result = articleService.getApprovedArticleByPublicIdAndLastVersion("test-id");

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        assertThat(result.getTitle()).isEqualTo("Approved Title");
        assertThat(result.getDescription()).isEqualTo("Approved Description");
        assertThat(result.getContent()).isEqualTo("Approved Content");
        assertThat(result.getVersion()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
    }

    @Test
    public void testGetApprovedArticleByPublicIdAndLastVersion_NotFound() {
        when(repository.findLatestApprovedArticleByPublicId("non-existent-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getApprovedArticleByPublicIdAndLastVersion("non-existent-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No approved article found with publicId: non-existent-id");
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_AndStatus_Success() {
        String publicId = "test-id";
        Integer version = 1;
        String status = "APPROVED";

        Article expectedArticle = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        when(repository.findArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status)))
                .thenReturn(Optional.of(expectedArticle));

        Article actualArticle = articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status));

        assertThat(actualArticle).isNotNull();
        assertThat(actualArticle.getPublicId()).isEqualTo(publicId);
        assertThat(actualArticle.getVersion()).isEqualTo(version);
        assertThat(actualArticle.getTitle()).isEqualTo("Test Title");
        assertThat(actualArticle.getDescription()).isEqualTo("Test Description");
        assertThat(actualArticle.getContent()).isEqualTo("Test Content");
        assertThat(actualArticle.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
        assertThat(actualArticle.getEditedBy()).isEqualTo("user");

        verify(repository, times(1)).findArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.APPROVED);
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_AndStatus_NotFound() {
        String publicId = "non-existent-id";
        Integer version = 1;
        String status = "APPROVED";

        when(repository.findArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No article found with publicId: " + publicId);

        verify(repository, times(1)).findArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.APPROVED);
    }

    @Test
    public void testUpdateArticle_Success() {
        ArticleRequestDto articleRequestDto = ArticleRequestDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .editedBy("user")
                .status(Article.ArticleStatus.APPROVED)
                .build();

        when(repository.findByPublicIdAndVersion("test-id", 1)).thenReturn(Optional.of(article));
        when(repository.save(any(Article.class))).thenReturn(article);

        Article updatedArticle = articleService.updateArticle("test-id", articleRequestDto, 1, true);

        assertThat(updatedArticle).isNotNull();
        assertThat(updatedArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedArticle.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedArticle.getContent()).isEqualTo("Updated Content");
        assertThat(updatedArticle.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
        assertThat(updatedArticle.getEditedBy()).isEqualTo("user");
        assertThat(updatedArticle.getVersion()).isEqualTo(1);
        assertThat(updatedArticle.getIsEditable()).isEqualTo(true);

        verify(repository).save(articleCaptor.capture());
        Article savedArticle = articleCaptor.getValue();
        assertThat(savedArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(savedArticle.getDescription()).isEqualTo("Updated Description");
        assertThat(savedArticle.getContent()).isEqualTo("Updated Content");
        assertThat(savedArticle.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
        assertThat(savedArticle.getEditedBy()).isEqualTo("user");
        assertThat(savedArticle.getVersion()).isEqualTo(1);
    }

    @Test
    public void testUpdateArticle_NotFound() {
        ArticleRequestDto articleRequestDto = ArticleRequestDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .editedBy("user")
                .status(Article.ArticleStatus.APPROVED)
                .build();

        when(repository.findByPublicIdAndVersion("non-existent-id", 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.updateArticle("non-existent-id", articleRequestDto, 1, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Article not found with PublicId: non-existent-id");
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_Success() {
        String publicId = "test-id";
        Integer version = 1;

        Article article = new Article();
        article.setPublicId(publicId);
        article.setVersion(version);
        article.setTitle("Test Title");
        article.setDescription("Test Description");
        article.setContent("Test Content");
        article.setStatus(Article.ArticleStatus.EDITING);

        when(repository.findByPublicIdAndVersion(publicId, version)).thenReturn(Optional.of(article));

        Article result = articleService.getArticleByPublicIdAndVersion(publicId, version);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);
        assertThat(result.getVersion()).isEqualTo(version);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);

        verify(repository, times(1)).findByPublicIdAndVersion(publicId, version);
    }


    @Test
    public void testGetArticleByPublicIdAndVersion_NotFound() {
        String publicId = "non-existent-id";
        Integer version = 1;

        when(repository.findByPublicIdAndVersion(publicId, version)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticleByPublicIdAndVersion(publicId, version))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Article not found with publicId: " + publicId);
    }
}
