package com.ashish.elasticsearch_project.sec03;

import com.ashish.elasticsearch_project.AbstractTest;
import com.ashish.elasticsearch_project.sec03.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryMethodsTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(QueryMethodsTest.class);

    @Autowired
    private ProductRepository repository;


}
