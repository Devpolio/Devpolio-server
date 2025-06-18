package com.spring.devpolio.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private String fileUrl; // 파일이 저장된 전체 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Builder
    public PortfolioFile(String originalFileName, String storedFileName, String fileUrl, Portfolio portfolio) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.portfolio = portfolio;
    }
}