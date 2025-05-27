package com.spring.devpolio.domain.portfolio.repository;

import com.spring.devpolio.domain.portfolio.entity.Portpolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortpolioRepository extends JpaRepository<Portpolio, Long> {
}
