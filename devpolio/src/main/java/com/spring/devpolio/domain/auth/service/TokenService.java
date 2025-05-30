package com.spring.devpolio.domain.auth.service;


import com.spring.devpolio.domain.auth.dto.LoginResponse;
import com.spring.devpolio.domain.auth.dto.RefreshAccessTokenRequest;
import com.spring.devpolio.domain.auth.dto.RefreshAccessTokenResponse;
import com.spring.devpolio.domain.auth.entity.RefreshToken;
import com.spring.devpolio.domain.auth.repository.RefreshTokenRepository;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JWTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 틀림");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public RefreshAccessTokenResponse reissueAccessToken(RefreshAccessTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.isValidToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("RefreshToken 유효하지 않음");
        }

        String email = jwtTokenProvider.getClaims(refreshToken).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RefreshToken savedToken = refreshTokenRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("저장된 RefreshToken 없음"));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("RefreshToken이 일치하지 않음");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        savedToken.setToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return new RefreshAccessTokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public String validateToken(String token) {
        if (!jwtTokenProvider.isValidToken(token)) {
            throw new IllegalArgumentException("Token Expired");
        }
        return "not expired";
    }

}
