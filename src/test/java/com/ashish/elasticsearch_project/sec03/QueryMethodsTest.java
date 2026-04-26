package com.ashish.elasticsearch_project.sec03;

import com.ashish.elasticsearch_project.AbstractTest;
import com.ashish.elasticsearch_project.sec03.entity.Product;
import com.ashish.elasticsearch_project.sec03.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class QueryMethodsTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(QueryMethodsTest.class);

    @Autowired
    private ProductRepository repository;

    @BeforeAll
    public void dataSetup(){
        var products = this.readResource("sec03/products.json", new TypeReference<List<Product>>() {
        });
        this.repository.saveAll(products);
        Assertions.assertEquals(20,this.repository.count());
    }

    @Test
    public void findByCategory(){
        var searchHits = this.repository.findByCategory("furniture");
        searchHits.stream().forEach(this.print());
        Assertions.assertEquals(4,searchHits.getTotalHits());
    }

    @Test
    public void findByCategories(){
        var searchHits = this.repository.findByCategoryIn(List.of("furniture","Beauty"));
        searchHits.stream().forEach(this.print());
        Assertions.assertEquals(4,searchHits.getTotalHits());
    }

}
