package com.spring.devpolio.domain.portfolio.controller;


import com.spring.devpolio.domain.portfolio.dto.addPortfolioDto;
import com.spring.devpolio.domain.portfolio.dto.addPortfolioResponseDto;
import com.spring.devpolio.domain.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/portfolio")
    public String portfolio() {
        return "portfolio";
    }
    @PostMapping(path = "/portfolio" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<addPortfolioResponseDto> addPortfolio(
            @RequestPart("portfolio") addPortfolioDto dto,
            @RequestPart("files") List<MultipartFile> files,
            Principal user
    ) {
       return ResponseEntity.ok(portfolioService.addPortfolio(dto, files, user));
    }


}
