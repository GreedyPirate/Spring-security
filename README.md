

# Spring boot实践之编写接口测试用例

>  测试用例对开发者降低bug率,方便测试人员回归测试有十分重要的意义。



本文介绍如何使用`MockMvc`编写测试用例. 在Spring boot项目中编写测试用例十分简单，通常建立一个Spring boot项目都会test目录下生成一个Test类

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Test
    public void contextLoads() throws Exception {
    }
}
```

以用户查询为例，通常有一个用户实体，以及`UserController`

```java
// @Data注解来自lombok
@Data
public class User {

    private Long id;

    private String username;

    private String password;
}
```

getInfo方法是一个restful接口，模拟查询用户详情

```java
@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("info")
    public User getInfo(@RequestParam(name = "name", required = true) String username){
        User user = new User();
        user.setUsername(username + "s");
        return user;
    }
}
```



以下通过MockMvc对象，测试`/user/info}`请求是否成功，并符合预期

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
                        //预测username的值为jays
                        .andExpect(jsonPath("$.username").value("jays"))
                        //获取响应体
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }
}
```

最终输出响应体

```json
{
	"id": 101,
	"username": "jays",
	"password": "1234"
}
```

关于`$.id`jsonpath的使用，参考[JsonPath](https://github.com/json-path/JsonPath)

同时付一段使用json参数的post请求方式，大同小异，

```java
String params = "{\"id\": 101,\"username\": \"jason\",\"password\": \"1234\"}";
mockMvc.perform(post("/user/login")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(params))
        .andExpect(status().isOk());
```

注意后端接受json格式参数的方式：`方法名(@RequestBody User user)` 



# Spring boot实践之封装返回体

在实际开发中，一个项目会形成一套统一的返回体接口规范，常见的结构如下

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "data": 真正的数据
}
```

读者可以根据自己的实际情况封装一个java bean，刑如：

```java
@Data
public class ResponseModel<T> {
    private T data;
    private Integer code;
    private String msg;
}
```

在spring boot中，会将返回的实体类，通过jackson自动转换成json，通过Spring提供的`ResponseBodyAdvice`接口拦截响应体，便可以实现

```java
public class ResponseAdvisor implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter, 
                                  MediaType mediaType,
                                  Class aClass, 
                                  ServerHttpRequest serverHttpRequest, 
                                  ServerHttpResponse serverHttpResponse) {
        ResponseModel model = new ResponseModel();
        model.setCode(0);
        model.setData(body);
        model.setMsg("SUCCESS");
        return model;
    }
}

```

这只是一个最初的功能，值得优化的地方有很多，读者应根据自己的情况进行扩展

根据笔者遇到的情况，抛砖引玉一下

1. 是否需要对所有的响应拦截，可以在supports方法中判断
2. 下载返回的是字节数据，再进行包装必然得不到正确的文件





# Spring boot实践之异常处理

在上一章[封装返回体]()中，已经对请求成功的情况进行了封装，接下来便是处理异常，服务的生产者需要通过状态码此次请求是否成功，出现异常时，错误信息是什么，形如:

```json
{
    "code": 1,
    "msg": "FAILED",
    "data": null
}
```

可以看出只需要`code`与`msg`, 参考 `org.springframework.http.HttpStatus`的实现，我们可以定义一个枚举来封装错误信息，对外暴露`getCode`，`getMsg`方法即可。由于异常属于一个基础模块，将这两个方法抽象到一个接口中。

错误接口

```java
public interface ExceptionEntity {

    Integer getCode();

    String getMsg();
}
```

以用户模块为例，所有用户相关的业务异常信息封装到`UserError`中，例如用户不存在，密码错误

```java
public enum UserError implements ExceptionEntity {

    NO_SUCH_USER(1, "用户不存在"),
    ERROR_PASSWORD(2, "密码错误"),
    ;

    private final Integer MODULE = 10000;

    private Integer code;

    private String msg;

    UserError(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return MODULE + this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}

```

需要注意的地方是笔者定义了一个`MODULE`字段，10000代表用户微服务，这样在拿到错误信息之后，可以很快定位报错的应用

自定义异常

```java
@Data
// lombok自动生成构造方法
@AllArgsConstructor
public class ServiceException extends RuntimeException{
    ExceptionEntity error;  
}
```

需要说明的是错误接口与自定义异常属于公共模块，而`UserError`属于用户服务

之后，便可以抛出异常

```java
throw new ServiceException(UserError.ERROR_PASSWORD);
```

目前来看，我们只是较为优雅的封装了异常，此时请求接口返回的仍然是Spring boot默认的错误体，没有错误信息

```java
{
    "timestamp": "2018-10-18T12:28:59.150+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "No message available",
    "path": "/user/error"
}
```

接下来的异常拦截方式，各路神仙都有自己的方法，笔者只说Spring boot项目中比较通用的`@ControllerAdvice`，由于是Restful接口，这里使用`@RestControllerAdvice`

```java
// 注意这属于基础模块，扫描路径不要包含具体的模块，用..代替
@RestControllerAdvice(basePackages="com.ttyc..controller",annotations={RestController.class})
// lombok的日志简写
@Slf4j
public class ControllerExceptionAdvisor{

    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel handleServiceException(ServiceException ex){
        Integer code = ex.getError().getCode();
        String msg = ex.getError().getMsg();
        log.error(msg);

        ResponseModel model = new ResponseModel();
        model.setCode(code);
        model.setMsg(msg);

        return model;
    }
    
    /**
     * 其他错误
     * @param ex
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel exception(Exception ex) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String msg = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        log.error(msg);

        ResponseModel model = new ResponseModel();
        model.setCode(code);
        model.setMsg(msg);

        return model;
    }
}
```

具有争议的一点是捕获`ServiceExcption`之后，应该返回200还是500的响应码，有的公司返回200，使用`code`字段判断成功失败，这完全没有问题，但是按照Restful的开发风格，这里的`@ResponseStatus`笔者返回了500，请读者根据自身情况返回响应码

### 测试接口与测试用例 

#### 测试接口

```java
    @GetMapping("error")
    public boolean error(){
        // 抛出业务异常示例
        throw new ServiceException(UserError.NO_SUCH_USER);
    }
```



#### 测试用例

```java
    @Test
    public void testError() throws Exception {
        String result =
                mockMvc.perform(get("/user/error"))
                        .andExpect(status().isInternalServerError())
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }
```

结果为:

```json
{
	"data": null,
	"code": 10001,
	"msg": "用户不存在"
}
```



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

如果还有n个接口需要校验username，你可能会抽取`if`语句到一个方法中，过段时间你又会发现，不光要校验username，还要password，adress等等一堆字段，总结起来

1. 重复劳动
2. 代码冗长，不利于阅读业务逻辑
3. 出现问题要去不同的接口中查看校验逻辑

这无疑是件让人崩溃的事情，此时作为一个开发人员，你已经意识到需要一个小而美的工具来解决这个问题，你可以去google，去github搜索这类项目，而不是毫无作为，抑或者是自己去造轮子

JSR303规范应运而生，其中比较出名的实现就是Hibernate Validator，已包含在`spring-boot-starter-web`其中,不需要重新引入，`javax.validation.constraints`包下常用的注解有

| 注解                           | 含义                                                         |
| :----------------------------- | :----------------------------------------------------------- |
| @NotNUll                       | 值不能为空                                                   |
| @Null                          | 值必须为空                                                   |
| @Pattern(regex=)               | 值必须匹配正则表达式                                         |
| @Size(min=,max=)               | 集合的大小必须在min~max之间，如List，数组                    |
| @Length(min=,max=)             | 字符串长度                                                   |
| @Range(min,max)                | 数字的区间范围                                               |
| @NotBlank                      | 字符串必须有字符                                             |
| @NotEmpty                      | 集合必须有元素，字符串                                       |
| @Email                         | 字符串必须是邮箱                                             |
| @URL                           | 字符串必须是url                                              |
| @AssertFalse                   | 值必须是false                                                |
| @AssertTrue                    | 值必须是true                                                 |
| @DecimalMax(value=,inclusive=) | 值必须小于等于(inclusive=true)/小于(inclusive=false) value属性指定的值。可以注解在字符串类型的属性上 |
| @DecimalMin(value=,inclusive=) | 值必须大于等于(inclusive=true)/大f (inclusive=false) value属性指定的值。可以注解在字符串类型的属性上 |
| @Digits(integer-,fraction=)    | 数字格式检查。integer指定整 数部分的最大长度，fraction指定小数部分的最大长度 |
| @Future                        | 值必须是未来的日期                                           |
| @Past                          | 值必须是过去的日期                                           |
| @Max(value=)                   | 值必须小于等于value指定的值。不能注解在字符串类型的属性上    |
| @Min(value=)                   | 值必须大于等于value指定的值。不能注解在字符串类型的属性上    |
| ...                            | ...                                                          |



接下来我们尝试一个入门例子,有一个User java bean, 为username字段加入@NotBlank注解，注意@NotBlank的包名

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

![进入断点](https://ws3.sinaimg.cn/large/006tNbRwly1fwdk366fb4j30s002eglr.jpg)

继续优化，想必大家也发现了，难道每个方法都要写`if`? 当然不用，ControllerAdvice不就是专门封装错误信息的吗，仿照[异常处理]()中的处理方式，我们很容易写出以下代码

```java
@ExceptionHandler({MethodArgumentNotValidException.class})
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ResponseModel exception(MethodArgumentNotValidException ex) {
    ResponseModel model = new ResponseModel();
    model.setCode(HttpStatus.BAD_REQUEST.value());
    model.setMsg(buildErrorMessage(ex));
    return model;
}

/**
 * 构建错误信息
 * @param ex
 * @return
 */
private String buildErrorMessage(MethodArgumentNotValidException ex){
    List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
    StringBuilder messageBuilder = new StringBuilder();
    objectErrors.stream().forEach(error -> {
        if(error instanceof FieldError){
            FieldError fieldError = (FieldError) error;
            messageBuilder.append(fieldError.getDefaultMessage()).append(",");
        }
    });
    String message  = messageBuilder.deleteCharAt(messageBuilder.length() - 1).toString();
    log.error(message);
    return message;
}
```

除了使用`@ExceptionHandler`来捕获`MethodArgumentNotValidException`以外，还可以覆盖`ResponseEntityExceptionHandler`抽象类的handleMethodArgumentNotValid方法，但是二者不可以混用



由于JSR303提供的注解有限，实际开发过程中校验往往需要结合实际需求，JSR303提供了自定义校验扩展接口

典型的一个请求场景是枚举类型参数，假设用户分为3类: 普通用户，VIP玩家，氪金玩家，分别用1，2，3表示，此时如何校验前端传入的值在范围内，抖机灵的朋友可能会想到@Range，万一是离散的不连续数呢？

自定义注解类

```java
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
// 指定校验类
@Constraint(validatedBy = InValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
    String message() default "必须在允许的数值内";

    int[] values();

    // 用于分组校验
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

注解的校验器

```java
import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class InValidator implements ConstraintValidator<In, Number> {// 校验Number类型 
	
	private Set<Integer> inValues;

    @Override
    public void initialize(In in) { 
    	inValues = Sets.newHashSet();
    	int[] arr = in.values();
    	for(int a : arr){
    		inValues.add(a);
    	}
    }

    @Override
    public boolean isValid(Number propertyValue, ConstraintValidatorContext cxt) {
        if(propertyValue==null) {
            return false;
        }
       return inValues.contains(propertyValue.intValue());
    }
}

```

至此，生产级别的参数校验基本完成



扩展

分组校验

在不同接口中，指定不同的校验规则，如：

1. 不同的接口，校验不同的字段
2. 同一个字段，在不同的接口中有不同的校验规则

以下实现第一种情况



首先定义两个空接口，代表不同的分组，也就是不同的业务

```java
public interface NewUser { }
public interface RMBUser { }
```

在指定校验规则时，指定分组

```java
public class User {
	// 省略...
    @NotBlank(groups = {NewUser.class}, message = "请输入密码")   
    private String password;

    @In(groups = {RMBUser.class}, values = {1,2,3}, message = "非法的用户类型")
    private Integer type;
}

```

不同的接口指定不同的校验分组

```java
// 省略类定义...
@PostMapping("normal")
public User normal(@Validated({NewUser.class}) @RequestBody User user){
    return user;
}

@PostMapping("rmb")
public User rmb(@Validated({RMBUser.class}) @RequestBody User user){
    return user;
}
```

编写测试用例

只检验密码

```java
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
```

输出：`{"data":null,"code":400,"msg":"请输入密码"}`
只检验用户类型

```java
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
```

输出：`{"data":null,"code":400,"msg":"非法的用户类型"}`



# Spring boot实践之异步编程





# Spring Security(一)：起步



配置：继承WebSecurityConfigurerAdapter



http basic：一个默认表单(浏览器自带？)，拦截器为BasicAuthenticationFilter，判断请求头有Authorization:Basic

formLogin：自定义登录表单，拦截器为UsernamePasswordAuthenticationFilter，判断参数包含username，password



拦截器顺序

1.UsernamePasswordAuthenticationFilter

2.BasicAuthenticationFilter

...

3.ExceptionTranslationFilter 发生AccessDeniedException，AuthenticationException异常时，由他处理

4.FilterSecurityInterceptor 最后的判断



第一版配置：

```
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .httpBasic() // basic认证
                .formLogin()
                .and()
                .authorizeRequests() //
                .anyRequest() //所有的请求
                .authenticated(); // 指定url可以被所有已认证用户访问
    }
}
```

#### 第一步：

使用form表单认证，输入根路径，一次/，一次/favicon.ico

但是"/"没有被permit，所以还是会进ExceptionTranslationFilter,最终进入默认的重定向页面"/login"

debug出来的顺序：

1. FilterSecurityInterceptor

2. ExceptionTranslationFilter#handleSpringSecurityException

   2.1 exception instanceof AccessDeniedException

3. LoginUrlAuthenticationEntryPoint#commence 

   3.1 LoginUrlAuthenticationEntryPoint的作用：ExceptionTranslationFilter用它开启一个由UsernamePasswordAuthenticationFilter认证的登录表单，

   通过loginFormUrl来设置登录表单url，可以使绝对，也可以是先对路径，用于重定向到登录页面

   如果是相对路径：原来的请求是http，通过设置forceHttps为true，认证之后还能访问http请求

4. 默认的重定向DefaultRedirectStrategy#sendRedirect

#### 第二步：

![](https://ws2.sinaimg.cn/large/006tNbRwly1fwhb0kdwgnj30ku07gwf5.jpg)

点击登录

执行顺序

1. UsernamePasswordAuthenticationFilter#attemptAuthentication

   1.1 setDetails(request, authRequest); 把request放进UsernamePasswordAuthenticationToken

2. 





























