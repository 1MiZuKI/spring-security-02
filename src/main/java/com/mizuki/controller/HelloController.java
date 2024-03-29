package com.mizuki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;

    @RequestMapping("/hello")
    public String hello() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("身份信息：" + authentication.getPrincipal());
        System.out.println("权限信息：" + authentication.getAuthorities());

        new Thread(() -> {
            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("子线程: " + authentication1.getPrincipal());
        }).start();

        return "hello security";
    }
}
