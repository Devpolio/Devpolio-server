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

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public void addLike(Long portfolioId) {
        User user = getCurrentUser();
        Portfolio portfolio = findPortfolioById(portfolioId);

        // 이미 '좋아요'를 눌렀는지 확인
        if (likeRepository.findByUserAndPortfolio(user, portfolio).isPresent()) {
            throw new IllegalStateException("이미 '좋아요'를 누른 포트폴리오입니다.");
        }

        likeRepository.save(new Like(user, portfolio));
    }

    @Transactional
    public void removeLike(Long portfolioId) {
        User user = getCurrentUser();
        Portfolio portfolio = findPortfolioById(portfolioId);

        // '좋아요' 기록을 찾아서 삭제
        Like like = likeRepository.findByUserAndPortfolio(user, portfolio)
                .orElseThrow(() -> new IllegalStateException("'좋아요'를 누른 기록이 없습니다."));

        likeRepository.delete(like);
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