package com.ikea.warehouse_query_api.service;

import com.ikea.warehouse_query_api.data.document.ArticleDocument;
import com.ikea.warehouse_query_api.data.document.ProductDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleAmount;
import com.ikea.warehouse_query_api.data.dto.ProductData;
import com.ikea.warehouse_query_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ArticleService articleService;


    public List<ProductData> getAllProducts(Pageable pageable) {

        Page<ProductDocument> productDocuments = productRepository.findAll(pageable);

        List<String> listOfArtId = productDocuments.stream().map(ProductDocument::containArticles)
                .flatMap(List::stream)
                .map(ArticleAmount::artId)
                .distinct()
                .toList();

        List<ArticleDocument> allArticlesById = articleService.getAllArticlesById(listOfArtId);

        return productDocuments.stream()
                .map(productDocument -> ProductData.builder()
                        .product(productDocument)
                        .quantity( getMinimumCountOfProductCanBeBuilt(productDocument, allArticlesById))
                        .build())
                .toList();
    }

    private Integer getMinimumCountOfProductCanBeBuilt(ProductDocument productDocument, List<ArticleDocument> allArticlesById) {
        return productDocument.containArticles().stream().mapToInt(articleAmount -> allArticlesById.stream()
                    .filter(filterArticleDocument -> filterArticleDocument.id().equals(articleAmount.artId()))
                    .findFirst()
                    .map(articleDocument1 -> Math.toIntExact(articleDocument1.stock() / articleAmount.amountOf()))
                    .orElse(0)
        ).min().orElse(0);
    }

}



