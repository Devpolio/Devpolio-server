package com.spring.devpolio.domain.file.controller; // 패키지 경로는 프로젝트에 맞게 조정하세요.

import com.spring.devpolio.domain.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/files") // "/api" 제거
@RequiredArgsConstructor
public class FileController {


    private final PortfolioService portfolioService;

    // application.properties 등에서 파일 저장 경로를 설정할 수 있습니다.
    @Value("${file.upload-dir}")
    private String uploadDirectory;


    /**
     * 이미지 미리보기(스트리밍)를 위한 API
     * @param filename 쿼리 파라미터로 전달된 서버 저장 파일명 (e.g., UUID_original_name.jpg)
     * @return 이미지 리소스
     */
    @GetMapping("/view")
    public ResponseEntity<Resource> viewFile(@RequestParam String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDirectory).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            try {
                String mimeType = Files.probeContentType(filePath);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .body(resource);
            } catch (IOException e) {
                log.error("Could not determine file type for file: {}", filename, e);
                return ResponseEntity.internalServerError().build();
            }
        } else {
            log.warn("File not found or not readable: {}", filename);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 파일 다운로드를 위한 API
     * @param filename 쿼리 파라미터로 전달된 서버 저장 파일명
     * @return 다운로드할 파일
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDirectory).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            // 원본 파일명을 헤더에 담기 위해 파일명에서 UUID 부분을 제거 (선택적)
            String originalFilename = filename.substring(filename.indexOf("_") + 1);
            String encodedOriginalFilename = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOriginalFilename + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deletePortfolioFile(
            @PathVariable Long fileId,
            Principal principal) {
        portfolioService.deletePortfolioFile(fileId, principal);
        return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
    }

}