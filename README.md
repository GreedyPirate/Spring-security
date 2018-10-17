# Spring boot实践之编写接口测试用例

首先需要提到的是[测试用例的重要性](https://blog.csdn.net/lyhdream/article/details/41152189),对我们开发者降低bug率,方便回归测试都有十分重要的意义，而在Spring boot项目中编写测试用例十分简单，通常建立一个Spring boot项目都会test目录下生成一个Test类，本文介绍如何使用`MockMvc`编写测试用例

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

    @GetMapping("info")
    public User getInfo(@RequestParam(name = "name", required = true) String username){
        User user = new User();
        user.setUsername(username.concat("s"));
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
}
```

最终输出响应体

```
{"id":101,"username":"jay","password":"1234"}
```

关于`$.id`jsonpath的使用，参考[JsonPath](https://github.com/json-path/JsonPath)

同时付一段使用json参数的post请求方式，大同小异，

```
String params = "{\"id\": 101,\"username\": \"jason\",\"password\": \"1234\"}";
mockMvc.perform(post("/user/login")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(params))
        .andExpect(status().isOk());
```

注意后端接受json格式参数的方式：`方法名(@RequestBody User user)` 哦



# Spring boot实践之封装返回体

在实际开发中，一个项目会形成一套统一的返回体接口规范，方便





# Spring boot实践之异常处理





# Spring boot实践之请求参数校验

本文讲述的是后端参数校验，在实际开发中，参数校验是前后端都要做的工作，因为请求接口的人除了普通用户，还有有各路神仙。

通常的校验代码如下

```java
@PostMapping("login")
public User login(@RequestBody User user){
    if(StringUtils.isBlank(user.getUsername())){
    	throw new RuntimeException("请输入用户名");
    }
    return user;
}
```

如果还有n个接口需要校验username，你可能会抽取`if`语句到一个方法中，过段时间你又会发现，不光要校验username，还要password，adress等等一堆字段。

这无疑是件让人崩溃的事情，此时作为一个开发人员，你已经意识到需要一个小而美的工具来解决这个问题，你可以去google，去github搜索这类项目，而不是毫无作为，抑或者是自己去造轮子

JSR303规范应运而生，其中比较出名的实现就是Hibernate Validator，接下来我们尝试一个入门例子

注解大全

//图片



有一个User java bean, 为username字段加入@NotBlank注解，注意@NotBlank的包名

```java
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class User {

    private Long id;

    @NotBlank(message = "请输入用户名")
    private String username;

    private String password;
}

```

表明将对username字段做非null，非空字符串校验，并为user参数添加@Valid

```java
public User login(@RequestBody @Valid User user)
```



按照[Spring boot实践之编写接口测试用例]()编写一个测试用例

```java
@Test
public void testBlankName() throws Exception {
    String params = "{\"id\": 101,\"username\": \"\",\"password\": \"1234\"}";
    mockMvc.perform(post("/user/login")
    .contentType(MediaType.APPLICATION_JSON_UTF8)
    .content(params))
    .andExpect(status().isBadRequest());
}
```

由于参数为空，将返回BadRequest—400响应码，但是此时我们获取不到错误信息，由于spring的拦截，甚至你会发现不进方法断点，仅仅得到一个400响应码，对前端提示错误信息帮助不大，因此我们需要获取错误信息

```java
    @PostMapping("login")
    public User login(@Valid @RequestBody User user, BindingResult result){
        if(result.hasErrors()){
            result.getFieldErrors().stream().forEach(error -> {
                // ....
            });
        }
        return user;
    }
```

此时我们发现已经进入方法断点

//图片

继续优化，想必大家也发现了，难道每个方法都要写`if`? 当然不用，ControllerAdvice不是专门封装错误信息的吗，根据[Spring boot实践之异常处理]()，我们很容易写出以下代码

```java

```



方法级别参数 https://www.cnblogs.com/beiyan/p/5946345.html

由于JSR303提供的注解有限，实际开发过程中校验往往需要结合实际需求，好在JSR303为我们提供了扩展

自定义校验





至此，生产级别的参数校验才算完成，很多文章写到BindingResult便结束了，人云亦云实在有点可惜，优化无止境，希望还能继续优化代码















# 



