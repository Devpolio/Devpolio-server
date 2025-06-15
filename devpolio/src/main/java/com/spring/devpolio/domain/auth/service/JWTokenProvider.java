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
    private final Dotenv dotenv = Dotenv.load();

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

    /**
     * [수정됨]
     * User 객체로부터 역할 목록(roles)을 가져와 토큰의 클레임에 추가합니다.
     * .add("role", "user") -> .add("roles", user.getRoles())
     */
    private String createToken(User user, Duration expiredAt, String tokenType) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiredAt.toMillis());

        // ▼▼▼ [수정] User 객체의 roles 필드가 null일 경우에 대비한 방어 코드 추가
        List<String> roles = Optional.ofNullable(user.getRoles()).orElse(Collections.emptyList());

        return Jwts.builder()
                .header().add(getHeader()).and()
                .claims()
                .issuedAt(now)
                .issuer(jwtProperties.getIssuer())
                .subject(user.getEmail())
                .expiration(exp)
                // ▼▼▼ [수정] 안정성이 확보된 roles 변수를 클레임에 추가합니다.
                .add("roles", roles)
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

    /**
     * [수정됨]
     * 토큰에서 역할 목록(roles)을 추출하여 Spring Security의 권한(GrantedAuthority) 객체 컬렉션으로 변환합니다.
     * 이 권한 정보를 사용하여 Authentication 객체를 생성합니다.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // "roles" 클레임에서 역할 목록(List)을 가져옵니다.
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.emptyList();
        }

        // 역할 목록을 사용하여 GrantedAuthority 컬렉션을 생성합니다.
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 생성할 때, authorities() 메소드를 사용하여 여러 권한을 부여합니다.
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(claims.getSubject())
                .password("") // 비밀번호는 토큰 인증에서 사용되지 않으므로 비워둡니다.
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return claims != null && "REFRESH".equals(claims.get("type", String.class));
    }
}
