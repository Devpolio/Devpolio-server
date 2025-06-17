package com.spring.devpolio.domain.portfolio.dto;

import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PortfolioDetailResponseDto {
    private final Long id;
    private final String title;
    private final String author;
    private final String category;
    private final boolean isPublic;
    private final LocalDateTime createdAt;
    private final List<PortfolioFileDto> files;

    public PortfolioDetailResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.title = portfolio.getTitle();
        this.author = portfolio.getAuthor();
        this.category = portfolio.getCategory();
        this.isPublic = portfolio.getIsPublic();
        this.createdAt = portfolio.getCreatedAt();

        this.files = portfolio.getFiles().stream()
                .map(PortfolioFileDto::new)
                .collect(Collectors.toList());
    }
}