package com.ikea.warehouse_query_api.service;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllArticlesById_delegatesToRepository() {
        List<String> ids = List.of("a1", "a2");
        List<ArticleDocument> expected = List.of();
        when(articleRepository.findByArticleIdList(ids)).thenReturn(expected);

        List<ArticleDocument> result = articleService.getAllArticlesById(ids);

        assertThat(result).isSameAs(expected);
        verify(articleRepository).findByArticleIdList(ids);
    }
}
