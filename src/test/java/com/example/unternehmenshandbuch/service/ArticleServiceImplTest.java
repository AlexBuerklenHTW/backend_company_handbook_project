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
    public void testCreateArticle_Success() {
        ArticleRequestDto articleRequestDto = ArticleRequestDto.builder()
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .editedBy("user")
                .build();

        when(repository.save(any(Article.class))).thenReturn(article);

        Article createdArticle = articleService.createArticle(articleRequestDto);

        System.out.println(createdArticle);

        assertThat(createdArticle).isNotNull();
        assertThat(createdArticle.getTitle()).isEqualTo("Test Title");
        assertThat(createdArticle.getDescription()).isEqualTo("Test Description");
        assertThat(createdArticle.getContent()).isEqualTo("Test Content");
        assertThat(createdArticle.getVersion()).isEqualTo(null);
        assertThat(createdArticle.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);

        verify(repository).save(articleCaptor.capture());
        Article savedArticle = articleCaptor.getValue();

        System.out.println(savedArticle);
        assertThat(savedArticle.getTitle()).isEqualTo("Test Title");
        assertThat(savedArticle.getDescription()).isEqualTo("Test Description");
        assertThat(savedArticle.getContent()).isEqualTo("Test Content");
        assertThat(savedArticle.getVersion()).isEqualTo(null);
        assertThat(savedArticle.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
    }


    @Test
    public void testGetArticleById_Success() {
        when(repository.findFirstByPublicId(anyString())).thenReturn(Optional.of(article));

        Article foundArticle = articleService.getArticleById("test-id");

        assertThat(foundArticle).isNotNull();
        assertThat(foundArticle.getPublicId()).isEqualTo("test-id");
        assertThat(foundArticle.getTitle()).isEqualTo("Test Title");
        assertThat(foundArticle.getDescription()).isEqualTo("Test Description");
        assertThat(foundArticle.getContent()).isEqualTo("Test Content");
        assertThat(foundArticle.getVersion()).isEqualTo(null);
        assertThat(foundArticle.getStatus()).isEqualTo(Article.ArticleStatus.EDITING);
    }

    @Test
    public void testGetArticleById_NotFound() {
        when(repository.findFirstByPublicId(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticleById("non-existent-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Article not found with id: non-existent-id");
    }

    @Test
    public void testGetAllArticles_Success() {
        when(repository.findByStatus(Article.ArticleStatus.SUBMITTED)).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByStatus();

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);
    }

    @Test
    public void testGetApprovedArticles_Success() {
        article.setStatus(Article.ArticleStatus.APPROVED);
        when(repository.findByStatus(Article.ArticleStatus.APPROVED)).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getApprovedArticles();

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);
    }


    @Test
    public void testSetApprovalStatus_Success() {
        Article approvedArticle = Article.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        when(repository.findByPublicIdAndEditedByAndVersionNull(anyString(), anyString())).thenReturn(Optional.of(article));
        when(repository.save(any(Article.class))).thenReturn(approvedArticle);

        Article result = articleService.setApprovalStatus("test-id", "APPROVED", 1, "user");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);

        verify(repository).save(articleCaptor.capture());
        Article savedArticle = articleCaptor.getValue();
        assertThat(savedArticle.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
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
    public void testGetArticlesByRoleAndStatus_Success() {
        when(repository.findByPublicIdAndStatus(anyString(), any(Article.ArticleStatus.class))).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByRoleAndStatus("test-id", "ADMIN");

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);
    }

    @Test
    public void testUpdateArticle_CompleteScenario() {
        ArticleRequestDto articleRequestDto = ArticleRequestDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("user123")
                .build();

        Article existingArticle = Article.builder()
                .publicId("test-id")
                .title("Old Title")
                .description("Old Description")
                .content("Old Content")
                .version(1)  // Initial version
                .status(Article.ArticleStatus.EDITING)
                .editedBy("user456")
                .build();


        Article expectedUpdatedArticle = Article.builder()
                .publicId("test-id")
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .version(2)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("user123")
                .build();


        when(repository.findFirstByPublicId("test-id")).thenReturn(Optional.of(existingArticle));
        when(repository.save(any(Article.class))).thenReturn(expectedUpdatedArticle);


        Article result = articleService.updateArticle("test-id", articleRequestDto, 2);


        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo("test-id");
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getContent()).isEqualTo("Updated Content");
        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(Article.ArticleStatus.SUBMITTED);
        assertThat(result.getEditedBy()).isEqualTo("user123");

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);


        verify(repository).findFirstByPublicId("test-id");
        verify(repository).save(articleCaptor.capture());


        Article savedArticle = articleCaptor.getValue();
        assertThat(savedArticle.getPublicId()).isEqualTo("test-id");
        assertThat(savedArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(savedArticle.getDescription()).isEqualTo("Updated Description");
        assertThat(savedArticle.getContent()).isEqualTo("Updated Content");
        assertThat(savedArticle.getVersion()).isEqualTo(2);
        assertThat(savedArticle.getStatus()).isEqualTo(Article.ArticleStatus.SUBMITTED);
        assertThat(savedArticle.getEditedBy()).isEqualTo("user123");
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
    public void testGetAllApprovedArticlesByPublicId_Success() {
        Article approvedArticle1 = Article.builder()
                .publicId("test-id")
                .title("Approved Title 1")
                .description("Approved Description 1")
                .content("Approved Content 1")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        Article approvedArticle2 = Article.builder()
                .publicId("test-id")
                .title("Approved Title 2")
                .description("Approved Description 2")
                .content("Approved Content 2")
                .version(2)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        when(repository.findAllApprovedArticlesByPublicId("test-id")).thenReturn(List.of(approvedArticle1, approvedArticle2));

        List<Article> result = articleService.getAllApprovedArticlesByPublicId("test-id");

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Approved Title 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Approved Title 2");
    }

    @Test
    public void testGetAllApprovedArticlesByPublicId_EmptyList() {
        when(repository.findAllApprovedArticlesByPublicId("test-id")).thenReturn(Collections.emptyList());

        List<Article> result = articleService.getAllApprovedArticlesByPublicId("test-id");

        assertThat(result).isEmpty();
    }

    @Test
    public void testGetArticlesByRoleAndStatus_UserRole_Success() {
        when(repository.findByPublicIdAndVersionNotNull(anyString())).thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByRoleAndStatus("test-id", "ROLE_USER");

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);

        verify(repository).findByPublicIdAndVersionNotNull("test-id");
        verify(repository, never()).findByPublicIdAndStatus(anyString(), any(Article.ArticleStatus.class));
    }

    @Test
    public void testGetArticlesByRoleAndStatus_OtherRole_Success() {
        when(repository.findByPublicIdAndStatus(anyString(), any(Article.ArticleStatus.class)))
                .thenReturn(Collections.singletonList(article));

        List<Article> articles = articleService.getArticlesByRoleAndStatus("test-id", "ADMIN");

        assertThat(articles).isNotEmpty();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0)).isEqualTo(article);

        verify(repository).findByPublicIdAndStatus("test-id", Article.ArticleStatus.SUBMITTED);
        verify(repository, never()).findByPublicIdAndVersionNotNull(anyString());
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_Success() {
        String publicId = "test-id";
        Integer version = 1;

        Article expectedArticle = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("user")
                .build();

        when(repository.findArticleByPublicIdAndVersion(publicId, version))
                .thenReturn(Optional.of(expectedArticle));

        Article actualArticle = articleService.getArticleByPublicIdAndVersion(publicId, version);

        assertThat(actualArticle).isNotNull();
        assertThat(actualArticle.getPublicId()).isEqualTo(publicId);
        assertThat(actualArticle.getVersion()).isEqualTo(version);
        assertThat(actualArticle.getTitle()).isEqualTo("Test Title");
        assertThat(actualArticle.getDescription()).isEqualTo("Test Description");
        assertThat(actualArticle.getContent()).isEqualTo("Test Content");
        assertThat(actualArticle.getStatus()).isEqualTo(Article.ArticleStatus.APPROVED);
        assertThat(actualArticle.getEditedBy()).isEqualTo("user");

        verify(repository, times(1)).findArticleByPublicIdAndVersion(publicId, version);
    }

    @Test
    public void testGetArticleByPublicIdAndVersion_NotFound() {
        String publicId = "non-existent-id";
        Integer version = 1;

        when(repository.findArticleByPublicIdAndVersion(publicId, version))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticleByPublicIdAndVersion(publicId, version))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No article found with publicId: " + publicId);

        verify(repository, times(1)).findArticleByPublicIdAndVersion(publicId, version);
    }
}
