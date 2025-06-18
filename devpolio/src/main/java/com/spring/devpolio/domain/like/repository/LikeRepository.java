package com.spring.devpolio.domain.like.repository;

import com.spring.devpolio.domain.like.entity.Like;
import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import com.spring.devpolio.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPortfolio(User user, Portfolio portfolio);

    List<Like> findByUserAndPortfolioIn(User user, List<Portfolio> portfolios);

}