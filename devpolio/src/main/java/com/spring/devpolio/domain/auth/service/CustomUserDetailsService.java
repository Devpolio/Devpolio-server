package com.spring.devpolio.domain.auth.service;

import com.spring.devpolio.domain.auth.dto.CustomUserDetails;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor; // Lombok 어노테이션 추가
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // ✨ final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // final 키워드는 필수입니다.

    // --- 아래 생성자 코드가 전부 사라짐 ---
    // public CustomUserDetailsService(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다: " + email));

        return new CustomUserDetails(user);
    }
}