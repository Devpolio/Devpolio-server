package com.spring.devpolio.config.filter; // 패키지 경로는 프로젝트에 맞게 조정하세요.

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 모든 필터 중 가장 먼저 실행되도록 순서 지정
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 모든 요청의 URI와 메소드를 로그로 기록합니다.
        // 말씀하신 이상한 URL(%08 등)도 이 로그에서 확인할 수 있습니다.
        log.info("[REQUEST] URI: {}, Method: {}", httpRequest.getRequestURI(), httpRequest.getMethod());

        // 다음 필터로 요청과 응답을 전달합니다. (이후 Spring Security 필터 등이 실행됨)
        chain.doFilter(request, response);
    }
}