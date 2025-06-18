package com.spring.devpolio.domain.admin.controller;

import com.spring.devpolio.domain.admin.dto.UserInfoDetailResponse;
import com.spring.devpolio.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserInfoDetailResponse>> getAllUsers(
            @Parameter(description = "전체 사용자 목록 조회")
            @MatrixVariable(required = false) String e
    ) {
        return ResponseEntity.ok(userService.getAllUsersInfo());
    }

    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<List<UserInfoDetailResponse>> getUserById(
            @Parameter(description = "id로 특정 사용자 조회")
            @PathVariable Long userId
    ) {

        UserInfoDetailResponse user = userService.getUserInfoById(userId);

        List<UserInfoDetailResponse> resultList = Collections.singletonList(user);

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/search/{search}")
    public ResponseEntity<List<UserInfoDetailResponse>> searchUsers(
            @Parameter(description = "이메일 또는 사용자 이름으로 사용자 조회")
            @MatrixVariable(pathVar = "search",required = false) String name,
            @MatrixVariable(pathVar = "search", required = false) String email
    ) {
        // 최종 확인을 위한 로그
        System.out.println("수신된 name: " + name);
        System.out.println("수신된 email: " + email);

        List<UserInfoDetailResponse> users = userService.searchUsers(name, email);
        return ResponseEntity.ok(users);
    }


}