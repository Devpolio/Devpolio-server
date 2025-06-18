package com.spring.devpolio.domain.portfolio.dto;


import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PortfolioDto {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private final int likeCount;
    private Boolean isLiked;

    public PortfolioDto(Portfolio portfolio, Boolean isLiked) {
        this.id = portfolio.getId();
        this.title = portfolio.getTitle();
        this.author = portfolio.getAuthor();
        this.createdAt = portfolio.getCreatedAt();
        this.likeCount = portfolio.getLikeCount();
        this.isLiked = isLiked;

    }

    public PortfolioDto(Portfolio portfolio) {
        this(portfolio, false);
    }

}
