package com.spring.devpolio.domain.user.controller;

import com.spring.devpolio.domain.admin.dto.UserInfoDetailResponse;
import com.spring.devpolio.domain.user.dto.UserInfoResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        UserInfoResponse userInfo = userService.getUserDetails(userDetails.getUsername());
        return ResponseEntity.ok(userInfo);
    }

}
