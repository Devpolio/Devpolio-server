package com.spring.devpolio.domain.file.controller; // 패키지 경로는 프로젝트에 맞게 조정하세요.

import com.spring.devpolio.domain.file.dto.FileDownloadDto;
import com.spring.devpolio.domain.file.service.FileService;
import com.spring.devpolio.domain.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {


    private final FileService fileService;
    private final PortfolioService portfolioService;

    // application.properties 등에서 파일 저장 경로를 설정할 수 있습니다.
    @Value("${file.upload-dir}")
    private String uploadDirectory;


    @GetMapping("/view")
    public ResponseEntity<Resource> viewFile(@RequestParam("filename") String filename) throws IOException {
        FileDownloadDto downloadDto = fileService.prepareDownload(filename);
        Resource resource = downloadDto.getResource();

        String originalFileName = downloadDto.getOriginalFileName();

        String contentType = Files.probeContentType(resource.getFile().toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 한글 파일명 깨지지 않게 조치
        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(originalFileName, StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) throws IOException {
        FileDownloadDto downloadDto = fileService.prepareDownload(filename);
        Resource resource = downloadDto.getResource();
        String originalFileName = downloadDto.getOriginalFileName();

        String contentType = Files.probeContentType(resource.getFile().toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 한글 파일명 깨지지 않게 조치
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(originalFileName, StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deletePortfolioFile(
            @PathVariable Long fileId,
            Principal principal) {
        portfolioService.deletePortfolioFile(fileId, principal);
        return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
    }

}