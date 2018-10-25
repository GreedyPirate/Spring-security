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

4.FilterSecurityInterceptor 最后的判断,根据config方法的配置判断



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
因为/ 既没有请求头，也没有username参数，所以先进FilterSecurityInterceptor

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


# Spring Security(二)：自定义用户认证逻辑

用户登录步骤：
1. 根据用户提交的username，从数据库中查询
2. 将用户提交的密码，和查询出来的密码对比

查看com.ttyc.security.browser.security.UserDetailServiceImplementation类
警告是因为spring security觉得密码没加密
Encoded password does not look like BCrypt

此时访问http://localhost:8090/test的步骤
1. 首先重定向到/login，输入账号密码
2. 登录失败，重复第一步
3. 登录成功，则真正请求到了/test

自定义表单注意点
1. 可以修改默认登录页面：loginPage("/sg-login.html")，但是要请求放行，即permitAll；登录必须是post请求
2. 可以修改默认的处理接口login：loginProcessingUrl("/deal-login")，页面的提交接口也要改成这个
也就是说默认的登录页面，还要登录接口也是默认的，要改也是改名字，不用自己实现

此时请求test流程
先调整到自定义的登录页，登录成功后看到返回结果



真正的请求是test，它是rest请求，万一输入的某一个html页面呢，应该按情况返回,即

1. rest请求返回一段json，提示前端引导用户到登录页面
2. html请求重定向到登录页面

UnauthorizedUserController的作用到底是什么？
前提：资源分为路由和api，test是api，html页面是路由
1. test作为一个rest接口，未认证的时候凭什么要"重定向"到登录页面，该做什么应该返回一个json让前端决定
2. html请求重定向到一个页面即可，这个页面url默认是基础模块的sg-login.html，每个应用服务可以通过i-security.browser.login-page自定义

登录之后只是跳转，应该返回用户信息
登录成功和失败的处理器
.successHandler(authSuccessHandler)
.failureHandler(authFailHandler)

Spring MVC工具类
1. 重定向工具类RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
2. request工具类ServletRequestUtils
3. request包装类ServletWebRequest

记住我功能
AbstractAuthenticationProcessingFilter#successfulAuthentication


