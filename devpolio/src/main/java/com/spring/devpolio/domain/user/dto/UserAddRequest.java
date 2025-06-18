package com.spring.devpolio.domain.user.dto;

import com.spring.devpolio.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;


@Getter
@Setter
public class UserAddRequest {
    private String name;
    private String email;
    private String password;


    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(encodedPassword) // 암호화된 비밀번호를 저장
                .roles(Collections.singletonList("ROLE_USER"))
                // admin의 경우 수동
                .build();
    }
}
