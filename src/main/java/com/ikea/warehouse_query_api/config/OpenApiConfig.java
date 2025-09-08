package com.ikea.warehouse_query_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse Query Service API")
                        .version("1.0.0")
                        .description("REST API for ingesting warehouse inventory and product data via file uploads. " +
                                   "This service supports both file upload and direct JSON payload endpoints for " +
                                   "inventory and product data ingestion with Kafka integration.")
                        .contact(new Contact()
                                .name("Mert UNSAL")
                                .email("mertunsal0@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server"),
                        new Server().url("https://api.warehouse.ikea.com").description("Production server")
                ));
    }

    @Bean
    public GroupedOpenApi legacy() {
        return GroupedOpenApi.builder()
                .group("legacy")
                .pathsToMatch("/api/commands/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
