package com.spring.devpolio.domain.portfolio.repository;

import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {
    Optional<PortfolioFile> findByStoredFileName(String storedFileName);
}