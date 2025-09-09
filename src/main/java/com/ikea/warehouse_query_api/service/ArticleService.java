package com.ikea.warehouse_query_api.service;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleDocument> getAllArticlesById(List<String> articleIds) {
        return articleRepository.findByArticleIdList(articleIds);
    }

}
