package com.elastic.mongo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;

@SpringBootApplication(exclude = ElasticsearchAutoConfiguration.class)
public class ElasticMongoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticMongoAppApplication.class, args);
	}

}
