package com.ashish.elasticsearch_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class AbstractTest {

	@Autowired
    private ObjectMapper mapper;

    @Autowired
    private ResourceLoader loader;

    protected<T> T readSource(String path, TypeReference<T> typeReference){
        try{
            var classpath = "classpath" + path;
            var file = this.loader.getResource(classpath).getFile();
            return this.mapper.readValue(file,typeReference);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
