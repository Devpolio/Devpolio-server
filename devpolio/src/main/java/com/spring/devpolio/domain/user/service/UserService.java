package com.spring.devpolio.domain.user.service;

import com.spring.devpolio.domain.user.dto.UserAddRequest;
import com.spring.devpolio.domain.user.dto.UserAddResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserAddResponse addUser(UserAddRequest request) {
        String password = bCryptPasswordEncoder.encode(request.getPassword());
        request.setPassword(password);
        User savedUser = userRepository.save(request.toEntity());
        return new UserAddResponse(savedUser.getId(), savedUser.getEmail());
    }

}
