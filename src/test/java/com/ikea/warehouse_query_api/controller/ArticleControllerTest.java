package com.ikea.warehouse_query_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleCommandRequest;
import com.ikea.warehouse_query_api.data.dto.ArticleResponse;
import com.ikea.warehouse_query_api.exception.GlobalExceptionHandler;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ArticleController.class)
@Import(GlobalExceptionHandler.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService service;

    @MockBean
    private ArticleMapper mapper;

    @Test
    void insert_ShouldReturnCreated() throws Exception {
        ArticleCommandRequest req = new ArticleCommandRequest("leg", 10L, "m1", Instant.EPOCH);
        ArticleDocument docToInsert = new ArticleDocument(null, req.name(), req.stock(), req.lastMessageId(), null, null, null, req.fileCreatedAt());
        ArticleDocument created = new ArticleDocument(new ObjectId(), "leg", 10L, "m1", 0L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        ArticleResponse resp = new ArticleResponse("id123", "leg", 10L, "m1", 0L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);

        when(mapper.toDocument(any(ArticleCommandRequest.class))).thenReturn(docToInsert);
        when(service.insert(any(ArticleDocument.class))).thenReturn(created);
        when(mapper.toResponse(created)).thenReturn(resp);

        mockMvc.perform(post("/api/v1/commands/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Correlation-ID", "corr-1")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id123"));
    }

    @Test
    void update_ShouldReturnOk() throws Exception {
        String id = new ObjectId().toHexString();
        ArticleCommandRequest req = new ArticleCommandRequest("arm", 5L, null, null);
        ArticleDocument updated = new ArticleDocument(new ObjectId(id), "arm", 5L, null, 2L, Instant.EPOCH, Instant.EPOCH, null);
        ArticleResponse resp = new ArticleResponse(id, "arm", 5L, null, 2L, Instant.EPOCH, Instant.EPOCH, null);
        when(mapper.toDocument(any(ArticleCommandRequest.class))).thenReturn(new ArticleDocument(null, "arm", 5L, null, null, null, null, null));
        when(service.update(any(ObjectId.class), any(ArticleDocument.class), eq(2L))).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(resp);

        mockMvc.perform(put("/api/v1/commands/articles/" + id)
                .param("expectedVersion", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        String id = new ObjectId().toHexString();
        mockMvc.perform(delete("/api/v1/commands/articles/" + id)
                .param("expectedVersion", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void insert_ShouldReturnBadRequest_OnValidationError() throws Exception {
        // name blank -> validation error
        ArticleCommandRequest req = new ArticleCommandRequest("", -1L, null, null);
        mockMvc.perform(post("/api/v1/commands/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("FILE_PROCESSING_ERROR"))
                .andExpect(jsonPath("$.message").value("Invalid products data"));
    }

    @Test
    void update_ShouldMapOptimisticLocking_To409() throws Exception {
        String id = new ObjectId().toHexString();
        ArticleCommandRequest req = new ArticleCommandRequest("arm", 5L, null, null);
        when(mapper.toDocument(any(ArticleCommandRequest.class))).thenReturn(new ArticleDocument(null, "arm", 5L, null, null, null, null, null));
        when(service.update(any(ObjectId.class), any(ArticleDocument.class), eq(2L)))
                .thenThrow(new OptimisticLockingFailureException("conflict"));

        mockMvc.perform(put("/api/v1/commands/articles/" + id)
                .param("expectedVersion", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("OPTIMISTIC_LOCK_CONFLICT"))
                .andExpect(jsonPath("$.path").value("/api/v1/commands/articles/" + id));
    }

    @Test
    void delete_ShouldMapIllegalArgument_To404() throws Exception {
        String id = new ObjectId().toHexString();
        // Simulate service throwing not found
        // void method -> use doThrow
        org.mockito.Mockito.doThrow(new IllegalArgumentException("Article not found: " + id))
                .when(service).delete(any(ObjectId.class), eq(1L));

        mockMvc.perform(delete("/api/v1/commands/articles/" + id).param("expectedVersion", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void insert_ShouldMapGenericException_To500() throws Exception {
        ArticleCommandRequest req = new ArticleCommandRequest("leg", 10L, null, null);
        when(mapper.toDocument(any(ArticleCommandRequest.class))).thenReturn(new ArticleDocument(null, "leg", 10L, null, null, null, null, null));
        when(service.insert(any(ArticleDocument.class))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/v1/commands/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"));
    }
}
