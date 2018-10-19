package com.ttyc.securitydemo;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityDemoApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void contextLoads() throws Exception {
        String result = mockMvc
                .perform(get("/user/101").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testQuery() throws Exception {
        String result =
                //执行get请求，这里有个小坑，第一个/必须有
                mockMvc.perform(get("/user/info")
                        //设置content-type请求头
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        //设置参数
                        .param("name", "jay"))
                        //预期的相应码是200-ok
                        .andExpect(status().isOk())
                        //预期的id值为101
                        .andExpect(jsonPath("$.username").value("jays"))
                        //获取响应体
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testBlankName() throws Exception {
        String params = "{\"id\": 101,\"username\": \"\",\"password\": \"1234\"}";
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInFaild() throws Exception {
        String params = "{\"id\": 101,\"username\": \"jay\",\"password\": \"1234\",\"type\": \"5\"}";
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInFaildMessage() throws Exception {
        String params = "{\"id\": 101,\"username\": \"jay\",\"password\": \"1234\",\"type\": \"5\"}";
        String result = mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testError() throws Exception {
        String result =
                mockMvc.perform(get("/user/error"))
                        .andExpect(status().isInternalServerError())
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }


    @Test
    public void testNormal() throws Exception {
        String params = "{\"id\": 101,\"username\": \"tom\",\"password\": \"\",\"type\": \"5\"}";
        String result = mockMvc.perform(post("/user/normal")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testRMB() throws Exception {
        String params = "{\"id\": 101,\"username\": \"tom\",\"password\": \"\",\"type\": \"5\"}";
        String result = mockMvc.perform(post("/user/rmb")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }


}
