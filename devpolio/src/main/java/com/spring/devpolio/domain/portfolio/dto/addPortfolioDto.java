package com.spring.devpolio.domain.portfolio.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
public class addPortfolioDto {
    private String title;
    private String password;
    private String category;
    private Boolean isPublic;
    private List<MultipartFile> file;
}
