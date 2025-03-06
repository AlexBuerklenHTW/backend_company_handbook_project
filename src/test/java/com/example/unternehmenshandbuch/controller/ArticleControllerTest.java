package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.config.SecurityConfig;
import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.controller.dto.ArticleStatusEditingAndVersionDto;
import com.example.unternehmenshandbuch.exception.ArticleValidationException;
import com.example.unternehmenshandbuch.exception.ResourceNotFoundException;
import com.example.unternehmenshandbuch.mapper.ArticleMapper;
import com.example.unternehmenshandbuch.model.Article;
import com.example.unternehmenshandbuch.service.AppUserDetailsServiceImpl;
import com.example.unternehmenshandbuch.service.ArticleService;
import com.example.unternehmenshandbuch.service.JwtService;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ArticleController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@Import(SecurityConfig.class)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private AppUserDetailsServiceImpl appUserDetailsServiceImpl;

    @MockBean
    private ArticleMapper articleMapper;

    @MockBean
    private JwtService jwtService;

    private Article article;
    private ArticleResponseDto articleResponseDto;

    @BeforeEach
    public void setUp() {
        article = Article.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .isEditable(false)
                .build();

        articleResponseDto = ArticleResponseDto.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .isEditable(false)
                .build();
    }

    @Test
    @WithMockUser
    public void testCreate_Success() throws Exception {
        when(articleService.createArticle(any(ArticleRequestDto.class))).thenReturn(article);
        when(articleMapper.mapToDto(any(Article.class))).thenReturn(articleResponseDto);

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Title\", \"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("test-id"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.status").value("EDITING"))
                .andExpect(jsonPath("$.editedBy").value("testUser"))
                .andExpect(jsonPath("$.isEditable").value(false));
    }

    @Test
    @WithMockUser
    public void testGetArticles_Approved_Success() throws Exception {
        Article approvedArticle = Article.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser")
                .build();

        ArticleResponseDto approvedArticleResponseDto = ArticleResponseDto.builder()
                .publicId("test-id")
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser")
                .build();

        when(articleService.getApprovedArticles()).thenReturn(Collections.singletonList(approvedArticle));
        when(articleMapper.mapToDtoList(anyList())).thenReturn(Collections.singletonList(approvedArticleResponseDto));

        mockMvc.perform(get("/articles/approved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicId").value("test-id"))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    @WithMockUser
    public void testGetArticles_Approved_EmptyList() throws Exception {
        when(articleService.getApprovedArticles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/articles/approved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    public void testSetSubmitStatus_NullTitle() throws Exception {
        String invalidArticleJson = "{\"title\": null, \"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}";

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidArticleJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testGetLatestArticleByPublicId_AndStatusEditedBy_Success() throws Exception {
        article.setVersion(2);
        article.setStatus(Article.ArticleStatus.APPROVED);

        articleResponseDto.setVersion(2);
        articleResponseDto.setStatus(Article.ArticleStatus.APPROVED);

        when(articleService.getLatestArticleByPublicId(anyString())).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleResponseDto);

        mockMvc.perform(get("/articles/test-id/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("test-id"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testSetSubmitStatus_MissingTitle() throws Exception {
        String invalidArticleJson = "{\"description\": \"Test Description\", \"content\": \"Test Content\", \"editedBy\": \"testUser\"}";

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidArticleJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testGetArticlesEditedByUser_Success() throws Exception {
        Article articleEditedByUser = Article.builder()
                .publicId("test-id")
                .title("User Edited Title")
                .description("User Edited Description")
                .content("User Edited Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleEditedByUserResponseDto = ArticleResponseDto.builder()
                .publicId("test-id")
                .title("User Edited Title")
                .description("User Edited Description")
                .content("User Edited Content")
                .version(1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        when(articleService.getArticlesByUserAndStatus(anyString(), any(Article.ArticleStatus.class)))
                .thenReturn(Collections.singletonList(articleEditedByUser));
        when(articleMapper.mapToDtoList(anyList()))
                .thenReturn(Collections.singletonList(articleEditedByUserResponseDto));

        mockMvc.perform(get("/articles/user/testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicId").value("test-id"))
                .andExpect(jsonPath("$[0].title").value("User Edited Title"))
                .andExpect(jsonPath("$[0].description").value("User Edited Description"))
                .andExpect(jsonPath("$[0].content").value("User Edited Content"))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[0].status").value("EDITING"))
                .andExpect(jsonPath("$[0].editedBy").value("testUser"));
    }


    @Test
    @WithMockUser
    public void testGetArticlesByStatusSubmitted_Success() throws Exception {
        Article submittedArticle = Article.builder()
                .publicId("test-id")
                .title("Submitted Title")
                .description("Submitted Description")
                .content("Submitted Content")
                .version(1)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        ArticleResponseDto submittedArticleResponseDto = ArticleResponseDto.builder()
                .publicId("test-id")
                .title("Submitted Title")
                .description("Submitted Description")
                .content("Submitted Content")
                .version(1)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        when(articleService.getArticlesByStatus()).thenReturn(Collections.singletonList(submittedArticle));
        when(articleMapper.mapToDtoList(anyList())).thenReturn(Collections.singletonList(submittedArticleResponseDto));

        mockMvc.perform(get("/articles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicId").value("test-id"))
                .andExpect(jsonPath("$[0].title").value("Submitted Title"))
                .andExpect(jsonPath("$[0].description").value("Submitted Description"))
                .andExpect(jsonPath("$[0].content").value("Submitted Content"))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"))
                .andExpect(jsonPath("$[0].editedBy").value("testUser"));
    }


    @Test
    @WithMockUser
    public void testGetApprovedArticleByPublicIdAndLastVersion_Success() throws Exception {
        Article latestApprovedArticle = Article.builder()
                .publicId("test-id")
                .title("Latest Approved Article Title")
                .description("Latest Approved Article Description")
                .content("Latest Approved Article Content")
                .version(3)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser3")
                .build();

        ArticleResponseDto latestApprovedArticleResponseDto = ArticleResponseDto.builder()
                .publicId("test-id")
                .title("Latest Approved Article Title")
                .description("Latest Approved Article Description")
                .content("Latest Approved Article Content")
                .version(3)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser3")
                .build();

        when(articleService.getApprovedArticleByPublicIdAndLastVersion(anyString())).thenReturn(latestApprovedArticle);
        when(articleMapper.mapToDto(any(Article.class))).thenReturn(latestApprovedArticleResponseDto);

        mockMvc.perform(get("/articles/test-id/approvedArticleByPublicIdAndLastVersion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value("test-id"))
                .andExpect(jsonPath("$.title").value("Latest Approved Article Title"))
                .andExpect(jsonPath("$.description").value("Latest Approved Article Description"))
                .andExpect(jsonPath("$.content").value("Latest Approved Article Content"))
                .andExpect(jsonPath("$.version").value(3))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.editedBy").value("testUser3"));
    }

    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_AndStatus_Success() throws Exception {
        String publicId = "test-id";
        Integer version = 1;
        String status = "APPROVED";

        when(articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status))).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleResponseDto);

        ResultActions result = mockMvc.perform(get("/articles/{publicId}/version/{version}/{status}", publicId, version, status)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value("test-id"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.status").value("EDITING"))
                .andExpect(jsonPath("$.editedBy").value("testUser"))
                .andDo(print());

        verify(articleService, times(1)).getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.APPROVED);
        verify(articleMapper, times(1)).mapToDto(article);
    }

    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_AndStatus_NotFound() throws Exception {
        String publicId = "non-existent-id";
        Integer version = 1;
        String status = "APPROVED";

        when(articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status)))
                .thenThrow(new ResourceNotFoundException("Article not found"));

        ResultActions result = mockMvc.perform(get("/articles/{publicId}/version/{version}/{status}", publicId, version, status)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(content().string("Article not found"))
                .andDo(print());

        verify(articleService, times(1)).getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.APPROVED);
        verify(articleMapper, times(0)).mapToDto(any());
    }

    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_InvalidVersionAndStatus() throws Exception {
        String publicId = "test-id";
        Integer version = -1;
        String status = "APPROVED";

        when(articleService.getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.valueOf(status)))
                .thenThrow(new IllegalArgumentException("Version must be positive"));

        ResultActions result = mockMvc.perform(get("/articles/{publicId}/version/{version}/{status}", publicId, version, status)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(content().string("Version must be positive"))
                .andDo(print());

        verify(articleService, times(1)).getArticleByPublicIdAndVersionAndStatus(publicId, version, Article.ArticleStatus.APPROVED);
        verify(articleMapper, times(0)).mapToDto(any());
    }

    @Test
    @WithMockUser
    public void testUpdateArticle_Success() throws Exception {
        String articleId = "test-id";
        Integer version = 1;
        Boolean isEditable = true;


        Article updatedArticle = Article.builder()
                .publicId(articleId)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .version(version + 1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        ArticleResponseDto updatedArticleDto = ArticleResponseDto.builder()
                .publicId(articleId)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .version(version + 1)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        when(articleService.updateArticle(eq(articleId), any(ArticleRequestDto.class), eq(version), eq(isEditable)))
                .thenReturn(updatedArticle);
        when(articleMapper.mapToDto(updatedArticle)).thenReturn(updatedArticleDto);

        String updateRequestJson = "{"
                + "\"title\": \"Updated Title\","
                + "\"description\": \"Updated Description\","
                + "\"content\": \"Updated Content\","
                + "\"editedBy\": \"testUser\""
                + "}";

        mockMvc.perform(post("/articles/{id}/{isEditable}", articleId, isEditable)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson)
                        .param("version", version.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(articleId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.version").value(version + 1))
                .andExpect(jsonPath("$.status").value("EDITING"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testUpdateArticle_NotFound() throws Exception {
        String articleId = "non-existent-id";
        Integer version = 1;
        Boolean isEditable = true;


        when(articleService.updateArticle(eq(articleId), any(ArticleRequestDto.class), eq(version), eq(isEditable)))
                .thenThrow(new ResourceNotFoundException("Article not found"));

        String updateRequestJson = "{"
                + "\"title\": \"Updated Title\","
                + "\"description\": \"Updated Description\","
                + "\"content\": \"Updated Content\","
                + "\"editedBy\": \"testUser\""
                + "}";

        mockMvc.perform(post("/articles/{id}/{isEditable}", articleId, isEditable)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson)
                        .param("version", version.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Article not found"));
    }

    @Test
    @WithMockUser
    public void testUpdateArticle_VersionConflict() throws Exception {
        String articleId = "test-id";
        Integer version = 1;
        Boolean isEditable = true;


        when(articleService.updateArticle(eq(articleId), any(ArticleRequestDto.class), eq(version), eq(isEditable)))
                .thenThrow(new ArticleValidationException("Version conflict"));

        String updateRequestJson = "{"
                + "\"title\": \"Updated Title\","
                + "\"description\": \"Updated Description\","
                + "\"content\": \"Updated Content\","
                + "\"editedBy\": \"testUser\""
                + "}";

        mockMvc.perform(post("/articles/{id}/{isEditable}", articleId, isEditable)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson)
                        .param("version", version.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Version conflict"));
    }

    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_Success() throws Exception {
        String publicId = "test-id";
        Integer version = 1;

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleDto = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        when(articleService.getArticleByPublicIdAndVersion(publicId, version)).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleDto);

        mockMvc.perform(get("/articles/{publicId}/{version}", publicId, version)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.status").value("EDITING"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }


    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_NotFound() throws Exception {
        String publicId = "non-existent-id";
        Integer version = 1;

        when(articleService.getArticleByPublicIdAndVersion(publicId, version))
                .thenThrow(new ResourceNotFoundException("Article not found"));

        mockMvc.perform(get("/articles/{publicId}/{version}", publicId, version)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Article not found"));
    }

    @Test
    @WithMockUser
    public void testGetArticleByPublicIdAndVersion_InvalidParameters() throws Exception {
        String publicId = "test-id";
        Integer version = -1;

        when(articleService.getArticleByPublicIdAndVersion(publicId, version))
                .thenThrow(new IllegalArgumentException("Version must be positive"));

        mockMvc.perform(get("/articles/{publicId}/{version}", publicId, version)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Version must be positive"));
    }

    @Test
    @WithMockUser
    public void testApproveArticle_Success() throws Exception {
        String publicId = "test-id";
        Integer version = 1;

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleDto = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .build();

        when(articleService.approveArticle(eq(publicId), any(ArticleRequestDto.class))).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleDto);

        mockMvc.perform(post("/articles/approval/{publicId}", publicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"someField\":\"someValue\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.status").value("EDITING"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testGetSubmittedArticleByPublicIdAndStatus_Success() throws Exception {
        String publicId = "test-id";
        String status = "submitted";

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleDto = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(1)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        when(articleService.getSubmittedArticleByPublicIdAndStatus(eq(publicId), eq(Article.ArticleStatus.SUBMITTED)))
                .thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleDto);

        mockMvc.perform(get("/articles/{publicId}/{status}/submittedArticleByPublicId", publicId, status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testGetAllApprovedArticlesByPublicId_Success() throws Exception {
        String publicId = "test-id";
        String status = "approved";

        Article article1 = Article.builder()
                .publicId(publicId)
                .title("Test Title 1")
                .description("Test Description 1")
                .content("Test Content 1")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser1")
                .build();

        Article article2 = Article.builder()
                .publicId(publicId)
                .title("Test Title 2")
                .description("Test Description 2")
                .content("Test Content 2")
                .version(2)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser2")
                .build();

        List<Article> articles = Arrays.asList(article1, article2);

        ArticleResponseDto articleDto1 = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title 1")
                .description("Test Description 1")
                .content("Test Content 1")
                .version(1)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser1")
                .build();

        ArticleResponseDto articleDto2 = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title 2")
                .description("Test Description 2")
                .content("Test Content 2")
                .version(2)
                .status(Article.ArticleStatus.APPROVED)
                .editedBy("testUser2")
                .build();

        List<ArticleResponseDto> articleDtos = Arrays.asList(articleDto1, articleDto2);

        when(articleService.getAllApprovedArticlesByPublicId(eq(publicId), eq(Article.ArticleStatus.APPROVED)))
                .thenReturn(articles);
        when(articleMapper.mapToDtoList(articles)).thenReturn(articleDtos);

        mockMvc.perform(get("/articles/{publicId}/approvedArticlesByPublicId/{status}", publicId, status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicId").value(publicId))
                .andExpect(jsonPath("$[0].title").value("Test Title 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[0].content").value("Test Content 1"))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].editedBy").value("testUser1"))
                .andExpect(jsonPath("$[1].publicId").value(publicId))
                .andExpect(jsonPath("$[1].title").value("Test Title 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].content").value("Test Content 2"))
                .andExpect(jsonPath("$[1].version").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].editedBy").value("testUser2"));
    }

    @Test
    @WithMockUser
    public void testSetSubmitStatus_Success() throws Exception {
        String publicId = "test-id";
        Integer version = 1;

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleDto = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .version(version)
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        when(articleService.setSubmitStatus(any(ArticleRequestDto.class))).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleDto);

        mockMvc.perform(post("/articles/submitting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"publicId\":\"" + publicId + "\", "
                                + "\"version\":" + version + ", "
                                + "\"status\":\"SUBMITTED\", "
                                + "\"title\":\"Test Title\", "
                                + "\"description\":\"Test Description\", "
                                + "\"content\":\"Test Content\", "
                                + "\"editedBy\":\"testUser\", "
                                + "\"isEditable\":true, "
                                + "\"isSubmitted\":true"
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.version").value(version))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testDeclineArticle_Success() throws Exception {
        String publicId = "test-id";
        String status = "SUBMITTED";
        String denyText = "The article was not accepted due to quality issues.";

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        ArticleResponseDto articleDto = ArticleResponseDto.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .status(Article.ArticleStatus.SUBMITTED)
                .editedBy("testUser")
                .build();

        when(articleService.declineArticleByPublicIdAndStatus(publicId, Article.ArticleStatus.SUBMITTED, denyText)).thenReturn(article);
        when(articleMapper.mapToDto(article)).thenReturn(articleDto);

        mockMvc.perform(post("/articles/decline/{publicId}/{status}/{denyText}", publicId, status, denyText)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.editedBy").value("testUser"));
    }

    @Test
    @WithMockUser
    public void testGetEditedByWithStatusEditingAndVersion_Success() throws Exception {
        String publicId = "test-id";

        Article article = Article.builder()
                .publicId(publicId)
                .title("Test Title")
                .description("Test Description")
                .content("Test Content")
                .status(Article.ArticleStatus.EDITING)
                .editedBy("testUser")
                .version(1)
                .build();

        ArticleStatusEditingAndVersionDto articleDto = ArticleStatusEditingAndVersionDto.builder()
                .editedBy("testUser")
                .version(1)
                .build();

        when(articleService.getEditedByWithStatusEditingAndVersion(publicId)).thenReturn(article);
        when(articleMapper.mapToStatusEditingAndVersion(article)).thenReturn(articleDto);

        mockMvc.perform(get("/articles/editedByWithStatusEditing/{publicId}", publicId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.editedBy").value("testUser"))
                .andExpect(jsonPath("$.version").value(1));
    }




}
