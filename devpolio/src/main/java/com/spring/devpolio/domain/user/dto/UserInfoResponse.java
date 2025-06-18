package com.spring.devpolio.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String name;
    private String email;
    private List<String> roles;
}