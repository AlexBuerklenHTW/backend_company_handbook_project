package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO: API-Spezifikationen erweitern/ergänzen

@Validated
public interface ArticleResource {

    @Operation(summary = "Create a new article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article created successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid article request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles")
    ResponseEntity<ArticleResponseDto> createArticle(@Valid @RequestBody ArticleRequestDto articleRequestDto);

    @Operation(summary = "Get all articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of articles", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles")
    ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusSubmitted();

    @Operation(summary = "Update an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid article request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles/{id}/{isEditable}")
    ResponseEntity<ArticleResponseDto> updateArticle(@PathVariable String id, @Valid @RequestBody ArticleRequestDto articleRequestDto, @RequestParam Integer version, @PathVariable Boolean isEditable);

    @Operation(summary = "Set approval status of an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article approval status updated", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid approval status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles/approval/{publicId}")
    ResponseEntity<ArticleResponseDto> approveArticle(@PathVariable String publicId, @RequestBody ArticleRequestDto articleRequestDto);

    @Operation(summary = "Get all approved articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of approved articles", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/approved")
    ResponseEntity<List<ArticleResponseDto>> getArticlesApproved();

    @Operation(summary = "Get the latest version of an article by public ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest version of the article found", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/latest")
    ResponseEntity<ArticleResponseDto> getLatestArticleByPublicIdAndStatusEditedBy(@PathVariable String publicId);

    @Operation(summary = "Get articles being edited by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of articles being edited by the user", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/user/{username}")
    ResponseEntity<List<ArticleResponseDto>> getArticlesEditedByUser(@PathVariable String username);

    @GetMapping("/articles/{publicId}/approvedArticleByPublicIdAndLastVersion")
    ResponseEntity<ArticleResponseDto> getApprovedArticleByPublicIdAndLastVersion(@PathVariable String publicId);

    @GetMapping("/articles/{publicId}/{status}/submittedArticleByPublicId")
    ResponseEntity<ArticleResponseDto> getSubmittedArticleByPublicIdAndStatus(@PathVariable String publicId, @PathVariable String status);

    @GetMapping("/articles/{publicId}/approvedArticlesByPublicId/{status}")
    ResponseEntity<List<ArticleResponseDto>> getAllApprovedArticlesByPublicId(@PathVariable String publicId, @PathVariable String status);

    @GetMapping("/articles/{publicId}/version/{version}/{status}")
    ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersionAndStatus(@PathVariable String publicId, @PathVariable Integer version, @PathVariable String status);

    @GetMapping("/articles/{publicId}/{version}")
    ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersion(@PathVariable String publicId, @PathVariable Integer version);

    @PostMapping("/articles/submitting")
    ResponseEntity<ArticleResponseDto> setSubmitStatus(@Valid @RequestBody ArticleRequestDto articleRequestDto);

    @PostMapping("/articles/decline/{publicId}/{status}")
    ResponseEntity<ArticleResponseDto> declineArticle(@PathVariable String publicId, @PathVariable String status);
}
