package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.controller.dto.ArticleStatusEditingAndVersionDto;
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
            @ApiResponse(responseCode = "201", description = "Article created successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid article request, validation failed", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles")
    ResponseEntity<ArticleResponseDto> createArticle(@Valid @RequestBody ArticleRequestDto articleRequestDto);

    @Operation(summary = "Get all articles that are in 'submitted' status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of articles with 'submitted' status", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles")
    ResponseEntity<List<ArticleResponseDto>> getArticlesByStatusSubmitted();

    @Operation(summary = "Update an existing article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid article request, validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles/{id}/{isEditable}")
    ResponseEntity<ArticleResponseDto> updateArticle(@PathVariable String id, @Valid @RequestBody ArticleRequestDto articleRequestDto, @RequestParam Integer version, @PathVariable Boolean isEditable);

    @Operation(summary = "Set approval status of an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article approval status updated successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid approval status provided", content = @Content),
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
            @ApiResponse(responseCode = "400", description = "Invalid username provided", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/user/{username}")
    ResponseEntity<List<ArticleResponseDto>> getArticlesEditedByUser(@PathVariable String username);

    @Operation(summary = "Get the approved version of an article by public ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approved version of the article found", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/approvedArticleByPublicIdAndLastVersion")
    ResponseEntity<ArticleResponseDto> getApprovedArticleByPublicIdAndLastVersion(@PathVariable String publicId);

    @Operation(summary = "Get submitted article by public ID and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submitted article found", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/{status}/submittedArticleByPublicId")
    ResponseEntity<ArticleResponseDto> getSubmittedArticleByPublicIdAndStatus(@PathVariable String publicId, @PathVariable String status);

    @Operation(summary = "Get all approved articles by public ID and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of approved articles by public ID and status", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/approvedArticlesByPublicId/{status}")
    ResponseEntity<List<ArticleResponseDto>> getAllApprovedArticlesByPublicId(@PathVariable String publicId, @PathVariable String status);

    @Operation(summary = "Get article by public ID, version, and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article found by public ID, version, and status", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/version/{version}/{status}")
    ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersionAndStatus(@PathVariable String publicId, @PathVariable Integer version, @PathVariable String status);

    @Operation(summary = "Get article by public ID and version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article found by public ID and version", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/{publicId}/{version}")
    ResponseEntity<ArticleResponseDto> getArticleByPublicIdAndVersion(@PathVariable String publicId, @PathVariable Integer version);

    @Operation(summary = "Set the submit status of an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article submit status set successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid submit status request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles/submitting")
    ResponseEntity<ArticleResponseDto> setSubmitStatus(@Valid @RequestBody ArticleRequestDto articleRequestDto);

    @Operation(summary = "Decline an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article declined successfully", content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid decline request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/articles/decline/{publicId}/{status}/{denyText}")
    ResponseEntity<ArticleResponseDto> declineArticle(@PathVariable String publicId, @PathVariable String status, @PathVariable String denyText);

    @Operation(summary = "Get the editor of an article with status 'EDITING'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Editor retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/articles/editedByWithStatusEditing/{publicId}")
    ResponseEntity<ArticleStatusEditingAndVersionDto> getEditedByWithStatusEditingAndVersion(@PathVariable String publicId);
}
