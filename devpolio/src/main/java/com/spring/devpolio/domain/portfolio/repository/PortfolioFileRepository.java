package com.spring.devpolio.domain.portfolio.repository;

import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {
}