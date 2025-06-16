package com.spring.devpolio.domain.portfolio.dto;

import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import lombok.Getter;


@Getter
public class PortfolioFileDto {
    private final Long id;
    private final String originalFileName;
    private final String fileUrl;

    public PortfolioFileDto(PortfolioFile portfolioFile) {
        this.id = portfolioFile.getId();
        this.originalFileName = portfolioFile.getOriginalFileName();
        this.fileUrl = portfolioFile.getFileUrl();
    }
}