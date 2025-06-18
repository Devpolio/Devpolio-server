package com.spring.devpolio.config.filter;

import com.spring.devpolio.domain.auth.service.JWTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JWTokenProvider tokenProvider;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/auth/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Vue에서 보내는 Options은 필터 안거치고 통과
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // ✅ [수정] 요청 URI에서 제어 문자를 제거하여 경로를 정규화합니다.
        String requestUri = request.getRequestURI();
        String normalizedUri = requestUri.replaceAll("\\p{Cntrl}", "");

        // 정규화된 URI로 제외할 경로인지 확인합니다.
        return EXCLUDE_URLS.stream().anyMatch(normalizedUri::startsWith);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getAccessToken(request);

        if (token != null && tokenProvider.isValidToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}