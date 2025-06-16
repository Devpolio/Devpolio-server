package com.spring.devpolio.domain.portfolio.repository;

import com.spring.devpolio.domain.portfolio.entity.Portfolio;
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

    /**
     * 공개된 포트폴리오 전체를 최신순으로 조회
     * @param isPublic 공개 여부 (true)
     * @return 포트폴리오 리스트
     */
    List<Portfolio> findAllByIsPublicOrderByCreatedAtDesc(boolean isPublic);

    /**
     * 특정 카테고리의 공개된 포트폴리오를 최신순으로 조회
     * @param category 카테고리명
     * @param isPublic 공개 여부 (true)
     * @return 포트폴리오 리스트
     */
    List<Portfolio> findAllByCategoryAndIsPublicOrderByCreatedAtDesc(String category, boolean isPublic);
}
