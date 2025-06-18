package com.spring.devpolio.domain.portfolio.dto;


import lombok.Getter;

@Getter
public class PortfolioUpdateRequest {
    private String title;
    private String author;
    private String category;
    private Boolean isPublic;



}
