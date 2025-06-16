package com.spring.devpolio.domain.user.service;

import com.spring.devpolio.domain.admin.dto.UserInfoResponse;
import com.spring.devpolio.domain.user.dto.UserAddRequest;
import com.spring.devpolio.domain.user.dto.UserAddResponse;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    // UserService.java (수정된 코드)
    public UserAddResponse addUser(UserAddRequest request) {
        // 1. 요청으로 받은 비밀번호를 직접 암호화합니다.
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // 2. toEntity() 메소드를 호출할 때, 암호화된 비밀번호를 파라미터로 전달합니다.
        User userToSave = request.toEntity(encodedPassword);

        // 3. 엔티티를 저장합니다.
        User savedUser = userRepository.save(userToSave);

        return new UserAddResponse(savedUser.getId(), savedUser.getEmail());
    }

    public List<UserInfoResponse> getAllUsersInfo(){

        return userRepository.findAll()
                .stream()
                .map(user -> new UserInfoResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRoles()
                        )
                )
                .collect(Collectors.toList());
    }

    public UserInfoResponse getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        return new UserInfoResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles());
    }

    public List<UserInfoResponse> searchUsers(String name, String email) {
        // 둘 다 비어있으면 검색하지 않고 빈 리스트를 반환
        if (name == null && email == null) {
            return Collections.emptyList();
        }

        return userRepository.findByNameOrEmail(name, email)
                .stream()
                .map(user -> new UserInfoResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
    }


}
