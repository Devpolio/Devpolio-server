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
                .subject(user.getEmail())
                .add("roles", roles)
                .add("type", tokenType)
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

            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        if (claims == null) {
            return null;
        }

        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.emptyList();
        }

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String username = claims.get("sub", String.class);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return claims != null && "REFRESH".equals(claims.get("type", String.class));
    }
}