package com.spring.devpolio.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshAccessTokenResponse {
    private String accessToken;
    private String refreshToken;


    public RefreshAccessTokenResponse(String newAccessToken) {
    }
}
