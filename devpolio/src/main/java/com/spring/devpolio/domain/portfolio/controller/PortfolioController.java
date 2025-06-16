package com.spring.devpolio.domain.portfolio.controller;


import com.spring.devpolio.domain.portfolio.dto.PortfolioDto;
import com.spring.devpolio.domain.portfolio.dto.addPortfolioDto;
import com.spring.devpolio.domain.portfolio.dto.addPortfolioResponseDto;
import com.spring.devpolio.domain.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Parameter;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.Port;
import java.awt.print.Pageable;
import java.security.Principal;
import java.util.List;

@RestController
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioDto>> getAllPortfolios(
            @RequestParam(required = false) String category) {
        List<PortfolioDto> portfolios = portfolioService.getAllPortfolios(category);
        return ResponseEntity.ok(portfolios);
    }

//    @GetMapping("/portfolio/{id}")
//    public ResponseEntity<T> portfolio(@PathVariable Long id) {
//
//    }


    @PostMapping(path = "/portfolio" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<addPortfolioResponseDto> addPortfolio(
            @Parameter(description = "포트폴리오 정보(title, category, isPublic, password)")
            @ModelAttribute addPortfolioDto dto,
            @Parameter(description = "배열<파일>")
            @RequestPart("files") List<MultipartFile> files,
            Principal user
    ) {
       return ResponseEntity.ok(portfolioService.addPortfolio(dto, files, user));
    }


}
