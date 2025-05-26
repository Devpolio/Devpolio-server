package com.spring.devpolio.domain.auth.dto;


import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // getter 생략 or Lombok @Getter 사용
}