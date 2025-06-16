package com.spring.devpolio.domain.user.repository;

import com.spring.devpolio.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);
   List<User> findByNameOrEmail(@Param("name") String name, @Param("email") String email);
}
