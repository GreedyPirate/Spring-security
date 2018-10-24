package com.ttyc.security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserSecurityController {

    @GetMapping("me")
    public UserDetails getDetail(@AuthenticationPrincipal UserDetails userDetails){
        return userDetails;
    }
}
