package com.ikea.warehouse_query_api.controller;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleCommandRequest;
import com.ikea.warehouse_query_api.data.dto.ArticleResponse;
import com.ikea.warehouse_query_api.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
@Tag(name = "Article Command Operations", description = "Endpoints for managing article inventory")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @Operation(summary = "Create a new Article")
    public ResponseEntity<ArticleResponse> insert(@Valid @RequestBody ArticleCommandRequest articleCommandRequest) {

        ArticleDocument created = articleService.save(articleCommandRequest);
        ArticleResponse response = articleMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Article")
    public ResponseEntity<ArticleResponse> update(
            @PathVariable("id") String id,
            @RequestParam(value = "expectedVersion", required = false) Long expectedVersion,
            @Valid @RequestBody ArticleCommandRequest request
    ) {
        ArticleDocument patch = articleMapper.toDocument(request);
        ArticleDocument updated = articleService.update(new ObjectId(id), patch, expectedVersion);
        ArticleResponse response = articleMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an Article")
    public void delete(
            @PathVariable("id") String id,
            @RequestParam(value = "expectedVersion", required = false) Long expectedVersion
    ) {
        articleService.delete(new ObjectId(id), expectedVersion);
    }
}
