# Spring boot中编写接口测试用例

首先需要提到的是[测试用例的重要性](https://blog.csdn.net/lyhdream/article/details/41152189),对我们开发者降低bug率,方便回归测试都有十分重要的意义，而在Spring boot项目中编写测试用例十分简单，通常建立一个Spring boot项目都会test目录下生成一个Test类，本文介绍如何使用`MockMvc`测试类编写测试用例

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Test
    public void contextLoads() throws Exception {
    }
}
```

以用户查询为例，通常有一个用户实体，以及`UserController` , 其中@Data注解来自lombok

```
@Data
public class User {

    private Long id;

    private String username;

    private String password;
}
```

query方法是一个restful接口，模拟查询用户详情, 并且使用正则校验id必须是数字

```java
@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("{id:\\d+}")
    public User query(@PathVariable(name = "id", required = true) Long id){
        User user = new User();
        user.setId(id);
        user.setPassword("1234");
        user.setUsername("jay");
        return user;
    }
}
```



以下通过MockMvc对象，测试`/user/{id}`请求是否成功，并符合预期

```java
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityDemoApplicationTests {

    //注入上下文对象
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        //初始化mockMvc对象
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void contextLoads() throws Exception {
        String result = mockMvc
            //执行get请求，设置content-type请求头
         .perform(get("/user/101").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            	//预期的相应码是200-ok
                .andExpect(status().isOk())
            	//预期的id值为101
                .andExpect(jsonPath("$.id").value(101))
            	//获取响应体
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }
}
```

最终输出响应体

```
{"id":101,"username":"jay","password":"1234"}
```

关于`$.id`jsonpath的使用，参考

