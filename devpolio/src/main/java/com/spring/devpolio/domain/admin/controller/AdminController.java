package com.spring.devpolio.domain.admin.controller;

import com.spring.devpolio.domain.admin.dto.UserInfoResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
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



    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersInfo());
    }

    // 이전에 논의했던 사용자 역할 변경 API
    // PATCH /admin/users/{userId}/roles
//    @PatchMapping("/users/{userId}/roles")
//    public ResponseEntity<Void> updateUserRoles(...) {
//        // ...
//    }
}