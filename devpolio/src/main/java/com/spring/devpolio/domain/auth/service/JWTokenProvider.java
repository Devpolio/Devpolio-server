package com.spring.devpolio.domain.auth.service;

import com.spring.devpolio.config.JwtProperties;
import com.spring.devpolio.domain.user.entity.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;


@RequiredArgsConstructor
@Service
public class JWTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey key;
    private JwtParser parser;
    private Dotenv dotenv = Dotenv.load();

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperties.getSecretKey()));
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

        return Jwts.builder()
                .header().add(getHeader()).and()
                .claims()
                .issuedAt(now)
                .issuer(jwtProperties.getIssuer())
                .subject(user.getEmail())
                .expiration(exp)
                .add("role", "user")
                .add("type", tokenType)
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private Map<String, Object> getHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
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
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String role = claims.get("role", String.class);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(claims.getSubject())
                .password(claims.getSubject()) // 임시 패스워드 (사용되지 않음)
                .roles(role)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return claims != null && "REFRESH".equals(claims.get("type", String.class));
    }
}