package com.ikea.warehouse_query_api.service.v1;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.repository.ArticleRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    @Mock
    private ArticleRepository repository;

    @InjectMocks
    private ArticleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldNullifyIdAndSave() {
        ArticleDocument input = new ArticleDocument(new ObjectId(), "leg", 10L, "msg-1", null, Instant.now(), Instant.now(), Instant.now());
        ArticleDocument saved = new ArticleDocument(new ObjectId(), input.name(), input.stock(), input.lastMessageId(), 0L, input.createdDate(), input.lastModifiedDate(), input.fileCreatedAt());
        when(repository.save(any())).thenReturn(saved);

        ArticleDocument result = service.save(input);

        ArgumentCaptor<ArticleDocument> captor = ArgumentCaptor.forClass(ArticleDocument.class);
        verify(repository).save(captor.capture());
        ArticleDocument toSave = captor.getValue();
        assertThat(toSave.id()).isNull();
        assertThat(toSave.name()).isEqualTo("leg");
        assertThat(result).isSameAs(saved);
    }

    @Test
    void update_ShouldUpdateFields_WhenVersionMatchesOrNull() {
        ObjectId id = new ObjectId();
        ArticleDocument existing = new ArticleDocument(id, "leg", 10L, "m1", 2L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        ArticleDocument patch = new ArticleDocument(null, "arm", 20L, "m2", null, null, null, null);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        ArticleDocument toReturn = new ArticleDocument(id, "arm", 20L, "m2", 2L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        when(repository.save(any())).thenReturn(toReturn);

        ArticleDocument result = service.update(id, patch, 2L);

        assertThat(result).isSameAs(toReturn);
        verify(repository).save(any(ArticleDocument.class));
    }

    @Test
    void update_ShouldThrow_WhenVersionMismatch() {
        ObjectId id = new ObjectId();
        ArticleDocument existing = new ArticleDocument(id, "leg", 10L, "m1", 3L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        ArticleDocument patch = new ArticleDocument(null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.update(id, patch, 2L))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Version mismatch for Article");
        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        ObjectId id = new ObjectId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new ArticleDocument(null, null, null, null, null, null, null, null), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Article not found");
    }

    @Test
    void delete_ShouldDelete_WhenVersionMatches() {
        ObjectId id = new ObjectId();
        ArticleDocument existing = new ArticleDocument(id, "leg", 10L, null, 1L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id, 1L);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_ShouldThrow_WhenVersionMismatch() {
        ObjectId id = new ObjectId();
        ArticleDocument existing = new ArticleDocument(id, "leg", 10L, null, 2L, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.delete(id, 1L))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Version mismatch for Article");
        verify(repository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        ObjectId id = new ObjectId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Article not found");
    }
}
