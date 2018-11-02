package com.ttyc.security.demo.controller;

import com.ttyc.security.demo.error.ServiceException;
import com.ttyc.security.demo.error.UserError;
import com.ttyc.security.demo.model.User;
import com.ttyc.security.demo.validator.NewUser;
import com.ttyc.security.demo.validator.RMBUser;
import lombok.experimental.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    ProviderSignInUtils providerSignInUtils;

    @PostMapping("/regist")
    public void regist(User user, HttpServletRequest request) {

        //不管是注册用户还是绑定用户，都会拿到一个用户唯一标识。
        String userId = user.getUsername();
        // 在这里就可以执行绑定或则注册用户的逻辑了
        // 然后使用 doPostSignUp 进行插入数据库
        providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
    }

    DeferredResult<User> userDeferredResult = new DeferredResult<>();

    @GetMapping("{id:\\d+}")
    public User query(@PathVariable(name = "id", required = true) Long id){
        User user = new User();

        Assert.notNull(user,"user can not be null");
        user.setId(id);
        user.setPassword("1234");
        user.setUsername("jay");
        return user;
    }

    @GetMapping("info")
    public User getInfo(@RequestParam(name = "name", required = true) String username){
        User user = new User();
        user.setId(101L);
        user.setPassword("1234");
        user.setUsername(username.concat("s"));
        return user;
    }

    @PostMapping("login")
    public User login(@Valid @RequestBody User user){
        return user;
    }

    @PostMapping("normal")
    public User normal(@Validated({NewUser.class}) @RequestBody User user){
        return user;
    }

    @PostMapping("rmb")
    public User rmb(@Validated({RMBUser.class}) @RequestBody User user){
        return user;
    }


    ////////////////////////asyn/////////////////////////////////////



    @GetMapping("callable")
    public Callable<User> callable(){

        return null;
    }

    @GetMapping("error")
    public boolean error(){
        throw new ServiceException(UserError.NO_SUCH_USER);
    }

    @GetMapping("check")
    public DeferredResult<User> check(){
        return userDeferredResult;
    }

    /**
     * 用这个请求的线程去设置result，check接口的结果才返回
     */
    @GetMapping("result")
    public void getResult(){
        User user = new User();
        user.setId(101L);
        user.setUsername("jay");
        this.userDeferredResult.setResult(user);
    }

    @GetMapping("thread")
    public DeferredResult<User> threadSet(){
        DeferredResult<User> result = new DeferredResult<>();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5L);
                User user = new User();
                user.setId(102L);
                user.setUsername("tom");
                result.setResult(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
        return result;
    }
}
