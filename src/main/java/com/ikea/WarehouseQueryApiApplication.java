package com.ikea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class WarehouseQueryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseQueryApiApplication.class, args);
	}

}
