package com.ikea.warehouse_query_api.controller;

import com.ikea.warehouse_query_api.data.dto.ProductData;
import com.ikea.warehouse_query_api.data.response.ProductResponse;
import com.ikea.warehouse_query_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "Product related operations")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    public List<ProductResponse> getProducts(@RequestParam Integer page, @RequestParam Integer size) {
        return productService.getAllProducts(page, size).stream().map(productData -> ProductResponse.builder()
                .id(productData.product().id().toString())
                .name(productData.product().name())
                .stock(productData.quantity())
                .build()
        ).toList();
    }
}

