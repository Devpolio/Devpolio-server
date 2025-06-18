package com.spring.devpolio.domain.file.service;


import com.spring.devpolio.domain.file.dto.FileDownloadDto;
import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import com.spring.devpolio.domain.portfolio.repository.PortfolioFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    private final PortfolioFileRepository portfolioFileRepository;

    @Value("${file.upload-dir}")
    private String uploadPath;


    public FileDownloadDto prepareDownload(String storedFileName) throws MalformedURLException {

        PortfolioFile portfolioFile = portfolioFileRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + storedFileName));


        Path filePath = Paths.get(uploadPath).resolve(storedFileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());


        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("파일을 읽을 수 없습니다: " + storedFileName);
        }

        return new FileDownloadDto(resource, portfolioFile.getOriginalFileName());
    }
}