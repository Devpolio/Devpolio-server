package com.spring.devpolio.domain.user.controller;

import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

//    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<String> getUserName(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }
}
