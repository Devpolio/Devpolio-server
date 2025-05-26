package com.spring.devpolio.domain.auth.controller;


import com.spring.devpolio.domain.auth.dto.LoginRequest;
import com.spring.devpolio.domain.auth.dto.LoginResponse;
import com.spring.devpolio.domain.auth.dto.RefreshAccessTokenRequest;
import com.spring.devpolio.domain.auth.dto.RefreshAccessTokenResponse;
import com.spring.devpolio.domain.auth.service.JWTokenProvider;
import com.spring.devpolio.domain.auth.service.TokenService;
import com.spring.devpolio.domain.user.dto.UserAddRequest;
import com.spring.devpolio.domain.user.dto.UserAddResponse;
import com.spring.devpolio.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final JWTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<UserAddResponse> signUp(@RequestBody UserAddRequest request){
        UserAddResponse response = userService.addUser(request);
        if(response == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest request){
        return ResponseEntity.ok(tokenService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> refreshToken(@RequestBody RefreshAccessTokenRequest request){
        String refreshToken = request.getRefreshToken();

        if(!tokenProvider.isValidToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(tokenService.reissueAccessToken(request));

    }







}
