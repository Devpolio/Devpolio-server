package com.spring.devpolio.domain.admin.controller;

import com.spring.devpolio.domain.admin.dto.UserInfoResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import com.spring.devpolio.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users") // 클래스 레벨에 /admin 경로를 설정
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersInfo());
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable Long userId) {
        UserInfoResponse user = userService.getUserInfoById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserInfoResponse>> searchUsers(
            @MatrixVariable(required = false) String name,
            @MatrixVariable(required = false) String email
    ) {
        // 최종 확인을 위한 로그
        System.out.println("!!!!!!!!!! /admin/users/search 컨트롤러에 성공적으로 도달함 !!!!!!!!!!");
        System.out.println("수신된 name: " + name);
        System.out.println("수신된 email: " + email);

        List<UserInfoResponse> users = userService.searchUsers(name, email);
        return ResponseEntity.ok(users);
    }

}