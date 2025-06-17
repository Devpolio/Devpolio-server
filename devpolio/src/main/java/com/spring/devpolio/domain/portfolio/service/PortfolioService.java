package com.spring.devpolio.domain.portfolio.service;


import com.spring.devpolio.domain.portfolio.dto.PortfolioDetailResponseDto;
import com.spring.devpolio.domain.portfolio.dto.PortfolioDto;
import com.spring.devpolio.domain.portfolio.dto.addPortfolioDto;
import com.spring.devpolio.domain.portfolio.dto.addPortfolioResponseDto;
import com.spring.devpolio.domain.portfolio.entity.Portfolio;
import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
import com.spring.devpolio.domain.portfolio.repository.PortfolioFileRepository;
import com.spring.devpolio.domain.portfolio.repository.PortfolioRepository;
import com.spring.devpolio.domain.user.entity.User;
import com.spring.devpolio.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;


@RequiredArgsConstructor
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioFileRepository portfolioFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public List<PortfolioDto> getAllPortfolios(String category) {
        List<Portfolio> portfolios;

        // 카테고리 파라미터가 있으면 카테고리별로, 없으면 전체 공개 포트폴리오를 조회
        if (category != null && !category.trim().isEmpty()) {
            portfolios = portfolioRepository.findAllByCategoryAndIsPublicOrderByCreatedAtDesc(category, true);
        } else {
            portfolios = portfolioRepository.findAllByIsPublicOrderByCreatedAtDesc(true);
        }

        // List<Entity>를 List<Dto>로 변환
        return portfolios.stream()
                .map(PortfolioDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getMyPortfolios() {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();


        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));


        List<Portfolio> portfolios = portfolioRepository.findByUser(currentUser);

        return portfolios.stream()
                .map(PortfolioDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResponseDto getPortfolio(Long portfolioId, Principal principal) {
        Portfolio portfolio = portfolioRepository.findByIdWithUserAndFiles(portfolioId)
                .orElseThrow(() -> new RuntimeException("해당 포트폴리오를 찾을 수 없습니다. ID: " + portfolioId));
        System.out.println("포트폴리오 찾는중");
        if (!portfolio.getIsPublic()) {
            if (principal == null) {
                throw new RuntimeException("접근 권한이 없습니다. 로그인이 필요합니다.");
            }
            String requestUserEmail = principal.getName();
            String ownerEmail = portfolio.getUser().getEmail();
            if (!requestUserEmail.equals(ownerEmail)) {
                throw new RuntimeException("비공개 포트폴리오에 대한 접근 권한이 없습니다.");
            }
        }
        System.out.println("포트폴리오 찾기 성공");
        return new PortfolioDetailResponseDto(portfolio);
    }

    public addPortfolioResponseDto addPortfolio(addPortfolioDto dto, List<MultipartFile> files, Principal principal) {
        String email = principal.getName(); // 기본적으로 username(email)을 반환
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(dto.getTitle());
        portfolio.setAuthor(dto.getAuthor());
        portfolio.setPassword(dto.getPassword());
        portfolio.setCategory(dto.getCategory());
        portfolio.setIsPublic(dto.getIsPublic());
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setUser(user);
        List<PortfolioFile> portfolioFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDirectory, uniqueName);
                try {
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("File save failed", e);
                }

                PortfolioFile pf = new PortfolioFile();
                pf.setFileUrl(path.toString());
                pf.setOriginalFileName(file.getOriginalFilename());
                pf.setPortfolio(portfolio);

                portfolioFiles.add(pf);
            }
        }
        portfolio.setFiles(portfolioFiles);
        portfolioRepository.save(portfolio); // cascade 덕분에 파일들도 같이 저장됨
        return new addPortfolioResponseDto(portfolio.getId(), "포트폴리오 저장 완료");
    }

    public void deletePortfolioFile(Long fileId, Principal principal) {
        PortfolioFile fileToDelete = portfolioFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다. ID: " + fileId));

        Authentication authentication = (Authentication) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        String requestUserEmail = principal.getName();
        String ownerEmail = fileToDelete.getPortfolio().getUser().getEmail();

        // 관리자가 아니고, 소유자도 아닐 경우에만 접근을 거부합니다.
        if (!isAdmin && !requestUserEmail.equals(ownerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("파일을 삭제할 권한이 없습니다.");
        }

        try {
            Path filePath = Paths.get(fileToDelete.getFileUrl());
            Files.deleteIfExists(filePath);
        } catch (IOException e) { // 모든 입출력 예외를 여기서 처리
            log.error("파일 시스템에서 파일 삭제 실패. Path: {}", fileToDelete.getFileUrl(), e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }

        portfolioFileRepository.delete(fileToDelete);
    }

    public void deletePortfolio(Long portfolioId, Principal principal) {
        // 1. 포트폴리오 정보를 DB에서 조회 (파일 정보까지 함께 fetch)
        Portfolio portfolioToDelete = portfolioRepository.findByIdWithUserAndFiles(portfolioId)
                .orElseThrow(() -> new RuntimeException("해당 포트폴리오를 찾을 수 없습니다. ID: " + portfolioId));

        // 2. 권한 확인 (관리자 또는 소유자)
        Authentication authentication = (Authentication) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        String requestUserEmail = principal.getName();
        String ownerEmail = portfolioToDelete.getUser().getEmail();

        if (!isAdmin && !requestUserEmail.equals(ownerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("파일을 삭제할 권한이 없습니다.");
        }

        // 3. 서버에 저장된 실제 파일들 삭제
        for (PortfolioFile file : portfolioToDelete.getFiles()) {
            try {
                Path filePath = Paths.get(file.getFileUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 하나의 파일 삭제에 실패하더라도 일단 로그만 남기고 계속 진행할 수 있습니다.
                // 또는 전체 작업을 중단하고 싶다면 여기서 RuntimeException을 던집니다.
                log.error("포트폴리오 전체 삭제 중 파일 삭제 실패. Path: {}", file.getFileUrl(), e);
            }
        }

        // 4. 포트폴리오 DB 레코드 삭제
        // Portfolio 엔티티에 cascade 설정이 되어 있으므로,
        // 포트폴리오가 삭제되면 연관된 PortfolioFile 레코드들도 함께 삭제됩니다.
        portfolioRepository.delete(portfolioToDelete);
    }

}