package com.spring.devpolio.domain.portfolio.dto;

import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import lombok.Getter;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;


@Getter
public class PortfolioFileDto {
    private final Long id;
    private final String originalFileName;
    private final String viewUrl;
    private final String downloadUrl;
    private final String storedFileName;

    public PortfolioFileDto(PortfolioFile portfolioFile) {
        this.id = portfolioFile.getId();
        this.originalFileName = portfolioFile.getOriginalFileName();

        String uniqueFileName = Paths.get(portfolioFile.getFileUrl()).getFileName().toString();

        String encodedFileName = UriUtils.encode(uniqueFileName, StandardCharsets.UTF_8);

        this.storedFileName = uniqueFileName;
        this.viewUrl = "/files/view?filename=" + encodedFileName;
        this.downloadUrl = "/files/download?filename=" + encodedFileName;
    }
}