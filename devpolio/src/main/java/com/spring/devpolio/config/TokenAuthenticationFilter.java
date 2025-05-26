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

        String token = getAccessToken(request);
        if (token != null){
            if(tokenProvider.isValidToken(token)){
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else{
                request.setAttribute("TokenException", "Invaild Token");
            }
        }
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
