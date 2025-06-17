package com.spring.devpolio.domain.auth.service;

import com.spring.devpolio.config.JwtProperties;
import com.spring.devpolio.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class JWTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey key;
    private JwtParser parser;

    @PostConstruct
    private void init() {
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parser().verifyWith(key).build();
    }

    public String createAccessToken(User user) {
        Duration duration = Duration.ofMinutes(jwtProperties.getDuration());
        return createToken(user, duration, "ACCESS");
    }

    public String createRefreshToken(User user) {
        Duration duration = Duration.ofDays(14);
        return createToken(user, duration, "REFRESH");
    }

    private String createToken(User user, Duration expiredAt, String tokenType) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiredAt.toMillis());

        // user.getRoles()가 null일 경우에 대비
        List<String> roles = Optional.ofNullable(user.getRoles()).orElse(Collections.emptyList());

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claims()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(exp)
                .subject(user.getEmail()) // 'sub' 클레임 설정
                .add("roles", roles)      // 'roles' 클레임 추가
                .add("type", tokenType)   // 'type' 클레임 추가
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱 실패 시 null을 반환하여 이후 로직에서 처리하도록 함
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        if (claims == null) {
            // 유효하지 않은 토큰이면 인증 객체를 생성하지 않음
            return null;
        }

        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.emptyList();
        }

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 이전 오류를 해결했던 방식인 claims.get("sub", String.class)를 사용
        String username = claims.get("sub", String.class);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("") // 토큰 기반 인증에서는 비밀번호가 필요 없음
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return claims != null && "REFRESH".equals(claims.get("type", String.class));
    }
}