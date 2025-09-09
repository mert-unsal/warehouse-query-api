package com.ikea.warehouse_query_api.controller;

import com.ikea.TestApplication;
import com.ikea.warehouse_query_api.data.document.ProductDocument;
import com.ikea.warehouse_query_api.data.dto.ArticleAmount;
import com.ikea.warehouse_query_api.data.dto.ProductData;
import com.ikea.warehouse_query_api.service.ProductService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ImportAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@org.springframework.test.context.ContextConfiguration(classes = {TestApplication.class, ProductController.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProducts_returnsTransformedResponse() throws Exception {
        ProductDocument pd = new ProductDocument(new ObjectId("650f1d2e3a4b5c6d7e8f9a0b"), "CHAIR",
                List.of(new ArticleAmount("a1", 4L)), null, null, null, null, null);
        ProductData data = ProductData.builder().product(pd).quantity(3).build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        when(productService.getAllProducts(pageable)).thenReturn(List.of(data));

        mockMvc.perform(get("/api/v1/products").param("page","0").param("size","10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("650f1d2e3a4b5c6d7e8f9a0b")))
                .andExpect(jsonPath("$[0].name", is("CHAIR")))
                .andExpect(jsonPath("$[0].stock", is(3)));
    }
}
