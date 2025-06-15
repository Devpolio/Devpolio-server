package com.spring.devpolio.domain.admin.controller;

import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin") // 클래스 레벨에 /admin 경로를 설정
public class AdminController {

    private final UserService userService;

//    // GET /admin/users
//    @GetMapping("/users")
//    public ResponseEntity<List<UserResponse>> getAllUsers() {
//        // 이 API는 ADMIN 역할을 가진 사용자만 호출할 수 있습니다.
//        List<User> users = userService.findAll();
//
//    }
//
//    // 이전에 논의했던 사용자 역할 변경 API
//    // PATCH /admin/users/{userId}/roles
//    @PatchMapping("/users/{userId}/roles")
//    public ResponseEntity<Void> updateUserRoles(...) {
//        // ...
//    }
}