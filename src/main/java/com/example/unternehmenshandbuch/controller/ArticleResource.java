package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.service.dto.ApprovalRequestDto;
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
    @PutMapping("/articles/{id}")
    ResponseEntity<ArticleResponseDto> updateArticle(@PathVariable String id, @Valid @RequestBody ArticleRequestDto articleRequestDto);

    @Operation(summary = "Set approval status of an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article approval status updated", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid approval status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/articles/{id}/approval")
    ResponseEntity<ArticleResponseDto> setApprovalStatus(@PathVariable String id, @RequestBody ApprovalRequestDto approvalRequestDto);

    @Operation(summary = "Get all versions of an article by public ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of article versions", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/versions")
    ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusAndRole(@PathVariable String publicId, @RequestParam String role);

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

    @GetMapping("/articles/{publicId}/approvedArticlesByPublicId")
    ResponseEntity<List<ArticleResponseDto>> getAllApprovedArticlesByPublicId(@PathVariable String publicId);

}
