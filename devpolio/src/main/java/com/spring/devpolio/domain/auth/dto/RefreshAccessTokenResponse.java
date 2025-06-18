package com.spring.devpolio.domain.auth.dto;



import lombok.Getter;

import lombok.Setter;


@Getter
@Setter
public class RefreshAccessTokenResponse {
    private String accessToken;
    private String refreshToken;


    public RefreshAccessTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
