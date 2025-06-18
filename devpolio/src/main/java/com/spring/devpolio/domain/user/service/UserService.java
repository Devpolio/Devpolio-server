package com.spring.devpolio.domain.user.service;

import com.spring.devpolio.domain.admin.dto.UserInfoDetailResponse;
import com.spring.devpolio.domain.user.dto.UserAddRequest;
import com.spring.devpolio.domain.user.dto.UserAddResponse;
import com.spring.devpolio.domain.user.dto.UserInfoResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;


    public UserAddResponse addUser(UserAddRequest request) {

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        User userToSave = request.toEntity(encodedPassword);

        User savedUser = userRepository.save(userToSave);

        return new UserAddResponse(savedUser.getId(), savedUser.getEmail());
    }

    public List<UserInfoDetailResponse> getAllUsersInfo(){

        return userRepository.findAll()
                .stream()
                .map(user -> new UserInfoDetailResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRoles()
                        )
                )
                .collect(Collectors.toList());
    }

    public UserInfoDetailResponse getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        return new UserInfoDetailResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles());
    }

    public List<UserInfoDetailResponse> searchUsers(String name, String email) {

        if (name == null && email == null) {
            return Collections.emptyList();
        }

        return userRepository.findByNameOrEmail(name, email)
                .stream()
                .map(user -> new UserInfoDetailResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserDetails(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        return new UserInfoResponse(user.getName(), user.getEmail(), user.getRoles());
    }




}
