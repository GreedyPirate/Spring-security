package com.ttyc.security.demo;

import com.alibaba.fastjson.JSONObject;
import com.ttyc.security.demo.model.MockJson;
import com.ttyc.security.demo.model.User;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.alibaba.fastjson.parser.Feature.AllowComment;

public class ApiTest {

    @Test
    public void testToString(){
        User user = new User();
        user.setPassword("1234");
        user.setUsername("jay");

        System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
    }

    @Test
    public void testDeserializedJson() throws IOException {
        Resource resource = new ClassPathResource("store.json");
        InputStream inputStream = resource.getInputStream();
        MockJson mockJson = JSONObject.parseObject(inputStream, Charset.forName("UTF-8"), MockJson.class, AllowComment);
        System.out.println(mockJson);
    }

}
