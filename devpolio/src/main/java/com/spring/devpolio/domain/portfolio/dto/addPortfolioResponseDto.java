package com.spring.devpolio.domain.portfolio.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class addPortfolioResponseDto {
    private Long id;
    private String message;

    public addPortfolioResponseDto(Long id, String msg) {
        this.id = id;
        this.message = msg;
    }
}
