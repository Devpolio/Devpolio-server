package com.spring.devpolio.domain.user.entity;


import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 합니다. protected로 안전하게 설정합니다.
@AllArgsConstructor // Builder를 위해 모든 필드를 사용하는 생성자를 추가합니다.
@Builder
@Table(name = "users") // 'user'는 DB 예약어일 수 있으므로 'users'로 테이블명을 지정하는 것이 좋습니다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // @Pattern은 DTO(Data Transfer Object) 계층에서 검증하는 것이 더 좋습니다.
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Portfolio> portfolio = new ArrayList<>(); // Null 방지를 위해 초기화

    // 참고: @Pattern 유효성 검사는 Controller에서 요청을 받는 DTO 클래스(예: SignupRequestDto)로 옮기는 것이
    // 계층 분리 원칙에 더 적합합니다.
}
