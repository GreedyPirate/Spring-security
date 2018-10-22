package com.ttyc.securitydemo.controller;

import com.ttyc.securitydemo.error.ServiceException;
import com.ttyc.securitydemo.error.UserError;
import com.ttyc.securitydemo.model.User;
import com.ttyc.securitydemo.thread.UserThread;
import com.ttyc.securitydemo.validator.NewUser;
import com.ttyc.securitydemo.validator.RMBUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("user")
public class UserController {

    DeferredResult<User> userDeferredResult = new DeferredResult<>();

    @GetMapping("{id:\\d+}")
    public User query(@PathVariable(name = "id", required = true) Long id){
        User user = new User();
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
