package com.spring.devpolio.domain.like.service;

import com.spring.devpolio.domain.like.entity.Like;
import com.spring.devpolio.domain.like.repository.LikeRepository;
import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import com.spring.devpolio.domain.portfolio.repository.PortfolioRepository;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public String addLike(Long portfolioId) {
        User user = getCurrentUser();
        Portfolio portfolio = findPortfolioById(portfolioId);

        if (likeRepository.findByUserAndPortfolio(user, portfolio).isPresent()) {
            return "ALREADY_LIKED";
        }

        likeRepository.save(new Like(user, portfolio));
        return "SUCCESS";
    }

    @Transactional
    public String removeLike(Long portfolioId) {
        User user = getCurrentUser();
        Portfolio portfolio = findPortfolioById(portfolioId);

        Optional<Like> like = likeRepository.findByUserAndPortfolio(user, portfolio);

        if (like.isEmpty()) {
            return "NOT_LIKED";
        }

        likeRepository.delete(like.get());
        return "SUCCESS";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));
    }

    private Portfolio findPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오를 찾을 수 없습니다."));
    }
}