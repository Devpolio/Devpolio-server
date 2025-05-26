package com.spring.devpolio.domain.user.dto;

import com.spring.devpolio.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserAddRequest {
    private String name;
    private String email;
    private String password;

    public User toEntity(){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}
