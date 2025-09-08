package com.ikea.warehouse_query_api.service.v1;

import com.ikea.warehouse_query_api.data.document.ProductDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleAmount;
import com.ikea.warehouse_query_api.data.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void insert_ShouldNullifyIdAndSave() {
        List<ArticleAmount> list = List.of(new ArticleAmount("1", 2L));
        ProductDocument input = new ProductDocument(new ObjectId(), "Chair", list, null, Instant.now(), Instant.now(), "a", "b");
        ProductDocument saved = new ProductDocument(new ObjectId(), "Chair", list, 0L, Instant.now(), Instant.now(), "a", "b");
        when(repository.save(any())).thenReturn(saved);

        ProductDocument result = service.insert(input);

        assertThat(result).isSameAs(saved);
        verify(repository).save(any(ProductDocument.class));
    }

    @Test
    void update_ShouldUpdateFields_WhenVersionMatches() {
        ObjectId id = new ObjectId();
        List<ArticleAmount> list = List.of(new ArticleAmount("1", 2L));
        ProductDocument existing = new ProductDocument(id, "Chair", list, 3L, Instant.EPOCH, Instant.EPOCH, "a", "b");
        ProductDocument patch = new ProductDocument(null, "Table", List.of(new ArticleAmount("2", 1L)), null, null, null, null, null);
        ProductDocument saved = new ProductDocument(id, "Table", List.of(new ArticleAmount("2", 1L)), 3L, Instant.EPOCH, Instant.EPOCH, "a", "b");
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(saved);

        ProductDocument result = service.update(id, patch, 3L);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void update_ShouldThrow_WhenVersionMismatch() {
        ObjectId id = new ObjectId();
        ProductDocument existing = new ProductDocument(id, "Chair", List.of(), 5L, Instant.EPOCH, Instant.EPOCH, "a", "b");
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(id, new ProductDocument(null, null, null, null, null, null, null, null), 4L))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Version mismatch for Product");
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        ObjectId id = new ObjectId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new ProductDocument(null, null, null, null, null, null, null, null), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void delete_ShouldDelete_WhenVersionMatches() {
        ObjectId id = new ObjectId();
        ProductDocument existing = new ProductDocument(id, "Chair", List.of(), 1L, Instant.EPOCH, Instant.EPOCH, "a", "b");
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id, 1L);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_ShouldThrow_WhenVersionMismatch() {
        ObjectId id = new ObjectId();
        ProductDocument existing = new ProductDocument(id, "Chair", List.of(), 2L, Instant.EPOCH, Instant.EPOCH, "a", "b");
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.delete(id, 1L))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("Version mismatch for Product");
        verify(repository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        ObjectId id = new ObjectId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }
}
