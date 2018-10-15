package com.ttyc.securitydemo.controller;

import com.ttyc.securitydemo.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("{id:\\d+}")
    public User getInfo(@PathVariable(required = true) Long id){
        User user = new User();
        user.setId(id);
        user.setPassword("1234");
        user.setUsername("jay");
        return user;
    }

    @PostMapping("login")
    public Boolean login(@RequestBody User user){
        System.out.println(user);
        return Boolean.TRUE;
    }
}
