package com.ashish.elasticsearch_project;

import org.springframework.boot.SpringApplication;

public class TestElasticsearchProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(ElasticsearchProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
