package com.spring.devpolio.domain.portfolio.repository;

import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import com.spring.devpolio.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> getPortfolioById(Long id);

    @Query("SELECT p FROM Portfolio p " +
            "JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.files " +
            "WHERE p.id = :portfolioId")
    Optional<Portfolio> findByIdWithUserAndFiles(@Param("portfolioId") Long portfolioId);

    List<Portfolio> findAllByIsPublicOrderByCreatedAtDesc(boolean isPublic);

    List<Portfolio> findAllByCategoryAndIsPublicOrderByCreatedAtDesc(String category, boolean isPublic);


    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.likes l LEFT JOIN FETCH l.user WHERE p.user = :user")
    List<Portfolio> findByUserFetchLikes(@Param("user") User user);

}
