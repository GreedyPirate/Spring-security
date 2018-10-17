package com.ttyc.securitydemo.controller;

import com.ttyc.securitydemo.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("info")
    public User getInfo(@RequestParam(name = "name", required = true) String username){
        User user = new User();
        user.setUsername(username.concat("s"));
        return user;
    }

    @PostMapping("login")
    public User login(@Valid @RequestBody User user, BindingResult result){
        if(result.hasErrors()){
            result.getFieldErrors().stream().forEach(error -> {
                // ....
            });
        }
        return user;
    }
}
