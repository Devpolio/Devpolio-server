package com.spring.devpolio.domain.like.entity;

import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import com.spring.devpolio.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // '좋아요'를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio; // '좋아요'의 대상이 된 포트폴리오

    // 생성자
    public Like(User user, Portfolio portfolio) {
        this.user = user;
        this.portfolio = portfolio;
    }
}