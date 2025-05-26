package com.spring.devpolio.domain.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RefreshAccessTokenRequest {
    private String refreshToken;
}
