package com.ashish.elasticsearch_project.sec03.repository;

import com.ashish.elasticsearch_project.sec03.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product,Integer> {
}
