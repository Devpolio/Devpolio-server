package com.spring.devpolio.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAddResponse {
    private Long id;
    private String email;


}
