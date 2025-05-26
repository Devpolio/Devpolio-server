package com.spring.devpolio.domain.auth.entity;

import com.spring.devpolio.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String token;
}

