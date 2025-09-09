package com.ikea.warehouse_query_api.service;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.document.ProductDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleAmount;
import com.ikea.warehouse_query_api.data.dto.ProductData;
import com.ikea.warehouse_query_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_calculatesMinimumBuildableQuantity() {
        // given
        ProductDocument chair = new ProductDocument(new org.bson.types.ObjectId(), "CHAIR", List.of(new ArticleAmount("a1", 4L), new ArticleAmount("a2", 8L)), null, null, null, null, null);
        ProductDocument table = new ProductDocument(new org.bson.types.ObjectId(), "TABLE", List.of(new ArticleAmount("a1", 1L), new ArticleAmount("a3", 2L)), null, null, null, null, null);
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<ProductDocument> productDocuments = new PageImpl<>(List.of(chair, table));
        when(productRepository.findAll(pageable)).thenReturn(productDocuments);

        List<ArticleDocument> articles = List.of(
                new ArticleDocument("a1", null, 16L, null, null, null, null, null), // supports 4 chairs or 16 tables (by a1)
                new ArticleDocument("a2", null, 7L, null, null, null, null, null),  // supports 0 chairs (since needs 8), limiting to 0
                new ArticleDocument("a3", null, 5L, null, null, null, null, null)   // supports 2 tables (integer division)
        );
        when(articleService.getAllArticlesById(List.of("a1", "a2", "a3"))).thenReturn(articles);

        // when
        List<ProductData> result = productService.getAllProducts(pageable);

        // then
        assertThat(result).hasSize(2);
        ProductData chairData = result.stream().filter(pd -> pd.product().name().equals("CHAIR")).findFirst().orElseThrow();
        ProductData tableData = result.stream().filter(pd -> pd.product().name().equals("TABLE")).findFirst().orElseThrow();

        assertThat(chairData.quantity()).isEqualTo(0); // limited by a2
        assertThat(tableData.quantity()).isEqualTo(2); // min(a1=16/1=16, a3=5/2=2) => 2
    }
    @Test
    void getAllProducts_missingArticleYieldsZeroQuantity() {
        // given
        Pageable pageable = Pageable.ofSize(5).withPage(0);

        ProductDocument stool = new ProductDocument(new org.bson.types.ObjectId(), "STOOL",
                List.of(new ArticleAmount("a1", 2L), new ArticleAmount("aX", 1L)), null, null, null, null, null);
        Page<ProductDocument> productDocuments = new PageImpl<>(List.of(stool));
        when(productRepository.findAll(pageable)).thenReturn(productDocuments);

        // articles fetched do NOT include aX
        List<ArticleDocument> articles = List.of(new ArticleDocument("a1", null, 5L, null, null, null, null, null));
        when(articleService.getAllArticlesById(List.of("a1", "aX"))).thenReturn(articles);

        // when
        List<ProductData> result = productService.getAllProducts(pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).quantity()).isEqualTo(0);
    }
}
