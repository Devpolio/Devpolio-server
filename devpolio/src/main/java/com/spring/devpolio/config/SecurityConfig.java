package com.spring.devpolio.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.devpolio.domain.auth.dto.TokenExceptionResponse;
import com.spring.devpolio.domain.auth.service.JWTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JWTokenProvider tokenProvider;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(jwtException()));
        return http.build();
    }

    private AuthenticationEntryPoint jwtException() {
        AuthenticationEntryPoint ap = (request, response, authException) -> {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            TokenExceptionResponse res = new TokenExceptionResponse();
            String message = (String) request.getAttribute("TokenException");
            if (message != null) { // UnAuthenticated
                response.setStatus(401);
                res.setResult(message);
            } else {
                response.setStatus(403);
                res.setResult(authException.getMessage());
            }
//            Gson gson = new Gson();
//            response.getWriter().write(gson.toJson(res));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(res));
        };
        return ap;
    }



}
