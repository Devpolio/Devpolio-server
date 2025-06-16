package com.spring.devpolio.config;

import com.spring.devpolio.domain.auth.service.JWTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {


    private final JWTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("===== TokenAuthenticationFilter 시작 =====");
        System.out.println("요청 URI: " + request.getRequestURI());

        String token = getAccessToken(request);
        System.out.println("추출된 토큰: " + token);

        if (token != null){
            System.out.println("토큰이 존재합니다. 유효성 검사를 시작합니다.");
            if(tokenProvider.isValidToken(token)){
                System.out.println("토큰이 유효합니다. 인증 객체를 생성합니다.");
                Authentication authentication = tokenProvider.getAuthentication(token);
                System.out.println("생성된 Authentication 객체: " + authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("SecurityContext에 인증 정보 설정 완료.");
            } else {
                System.out.println("토큰이 유효하지 않습니다.");
                request.setAttribute("TokenException", "Invaild Token");
            }
        } else {
            System.out.println("헤더에서 토큰을 찾을 수 없습니다.");
        }

        System.out.println("===== TokenAuthenticationFilter 종료. 다음 필터로 이동. =====");


        filterChain.doFilter(request, response);

    }

    private String getAccessToken(HttpServletRequest request) {
        String AccessToken = request.getHeader("Authorization");

        if(AccessToken != null && AccessToken.startsWith("Bearer ")){
            return AccessToken.substring("Bearer ".length());
        }
        return null;
    }
}
