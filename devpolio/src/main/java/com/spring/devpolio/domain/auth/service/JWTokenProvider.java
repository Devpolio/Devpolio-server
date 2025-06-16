package com.spring.devpolio.domain.auth.service;

import com.spring.devpolio.config.JwtProperties;
import com.spring.devpolio.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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

    /**
     * 지정된 사용자에 대한 액세스 토큰을 생성합니다.
     *
     * @param user 토큰을 생성할 사용자 정보
     * @return 생성된 액세스 토큰 문자열
     */
    public String createAccessToken(User user) {
        Duration duration = Duration.ofMinutes(jwtProperties.getDuration());
        return createToken(user, duration, "ACCESS");
    }

    /**
     * 지정된 사용자에 대한 리프레시 토큰을 생성합니다.
     *
     * @param user 토큰을 생성할 사용자 정보
     * @return 생성된 리프레시 토큰 문자열
     */
    public String createRefreshToken(User user) {
        Duration duration = Duration.ofDays(14);
        return createToken(user, duration, "REFRESH");
    }

    /**
     * JWT 토큰을 생성하는 핵심 메서드.
     *
     * @param user      사용자 정보
     * @param expiredAt 토큰 만료 시간
     * @param tokenType 토큰 타입 (ACCESS, REFRESH)
     * @return 생성된 JWT 문자열
     */
    private String createToken(User user, Duration expiredAt, String tokenType) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiredAt.toMillis());

        // Null-safe하게 사용자의 역할 목록을 가져옵니다.
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
                .add("roles", roles) // 역할 목록을 클레임에 추가
                .add("type", tokenType)
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }


    /**
     * 제공된 토큰이 유효한지 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token) {
        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱 중 예외 발생 시 유효하지 않은 토큰으로 간주
            return false;
        }
    }

    /**
     * 토큰에서 클레임(Payload) 정보를 추출합니다.
     *
     * @param token 클레임을 추출할 JWT 토큰
     * @return 추출된 클레임 객체. 유효하지 않은 토큰일 경우 null 반환.
     */
    Claims getClaims(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰은 클레임을 추출할 수 없으므로 null 반환
            return null;
        }
    }

    /**
     * 토큰으로부터 인증(Authentication) 객체를 생성하여 반환합니다.
     *
     * @param token 인증 객체를 생성할 JWT 토큰
     * @return 생성된 Authentication 객체. 토큰이 유효하지 않으면 null 반환.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // ✅ [수정] claims가 null일 경우(토큰이 유효하지 않을 경우) NullPointerException 방지
        if (claims == null) {
            return null;
        }

        // "roles" 클레임에서 역할 목록(List)을 가져옵니다.
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.emptyList();
        }

        // 역할 목록을 사용하여 Spring Security의 GrantedAuthority 컬렉션을 생성합니다.
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Spring Security의 UserDetails 객체를 생성합니다.
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(claims.getSubject())
                .password("") // 토큰 기반 인증에서는 비밀번호가 필요 없습니다.
                .authorities(authorities)
                .build();

        // UserDetails와 권한 정보를 바탕으로 Authentication 객체를 생성하여 반환합니다.
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * 토큰이 리프레시 토큰인지 확인합니다.
     *
     * @param token 확인할 JWT 토큰
     * @return 리프레시 토큰이면 true, 그렇지 않으면 false
     */
    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        // ✅ [수정] claims가 null일 경우를 대비한 체크 추가 (기존 코드에도 있었지만 명시적으로 확인)
        return claims != null && "REFRESH".equals(claims.get("type", String.class));
    }
}
