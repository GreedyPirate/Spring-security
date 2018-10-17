package com.ttyc.securitydemo;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;


public class JsonPathTest {

    @Test
    public void testReadRoot() throws IOException {
        Resource resource = new ClassPathResource("store.json");
        String json = IOUtils.toString(resource.getInputStream(), "UTF-8");
        List<String> authors = JsonPath.read(json, "$.store.book[*].author");
        authors.stream().forEach(System.out::println);
    }
}
