package com.spring.devpolio.domain.portfolio.service;


import com.spring.devpolio.domain.like.repository.LikeRepository;
import com.spring.devpolio.domain.portfolio.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;


@RequiredArgsConstructor
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioFileRepository portfolioFileRepository;
    private final LikeRepository likeRepository;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Transactional(readOnly = true)
    public List<PortfolioDto> getAllPortfolios(String category) {

        User currentUser = getCurrentUser();

        // 단순 쿼리로 전체 불러오기
        List<Portfolio> portfolios = (category != null && !category.trim().isEmpty())
                ? portfolioRepository.findAllByCategoryAndIsPublicOrderByCreatedAtDesc(category, true)
                : portfolioRepository.findAllByIsPublicOrderByCreatedAtDesc(true);

        if (portfolios.isEmpty()) {
            return Collections.emptyList();
        }

        //    (성능 최적화를 위해 '좋아요' 누른 포트폴리오의 ID만 Set으로 저장)
        Set<Long> likedPortfolioIds = likeRepository.findByUserAndPortfolioIn(currentUser, portfolios)
                .stream()
                .map(like -> like.getPortfolio().getId())
                .collect(Collectors.toSet());

        return portfolios.stream()
                .map(portfolio -> new PortfolioDto(
                        portfolio,
                        likedPortfolioIds.contains(portfolio.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> getMyPortfolios() {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        List<Portfolio> portfolios = portfolioRepository.findByUserFetchLikes(currentUser);

        return portfolios.stream()
                .map(portfolio -> {
                    System.out.println("포트폴리오 ID " + portfolio.getId() + " 처리 중..."); // 디버깅용 로그

                    boolean isLikedByCurrentUser = portfolio.getLikes().stream()
                            .anyMatch(like -> {
                                // 비교하는 ID를 직접 출력하여 확인합니다.
                                System.out.println("  -> 좋아요 누른 사용자 ID: " + like.getUser().getId());
                                return like.getUser().getId().equals(currentUser.getId());
                            });

                    System.out.println("  -> isLiked 계산 결과: " + isLikedByCurrentUser); // 디버깅용 로그
                    return new PortfolioDto(portfolio, isLikedByCurrentUser);
                })
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
    @Transactional // 데이터 변경 트랜젝션
    public addPortfolioResponseDto addPortfolio(addPortfolioDto dto, List<MultipartFile> files, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

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
            if (file != null && !file.isEmpty()) {

                String originalFilename = file.getOriginalFilename();
                String uniqueName = UUID.randomUUID().toString() + "_" + originalFilename;
                Path path = Paths.get(uploadDirectory, uniqueName);

                try {
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장에 실패했습니다.", e);
                }

                PortfolioFile pf = PortfolioFile.builder()
                        .originalFileName(originalFilename)
                        .storedFileName(uniqueName)
                        .fileUrl(path.toString())
                        .portfolio(portfolio)
                        .build();

                portfolioFiles.add(pf);
            }
        }

        portfolio.setFiles(portfolioFiles);

        portfolioRepository.save(portfolio);

        return new addPortfolioResponseDto(portfolio.getId(), "포트폴리오 저장 완료");
    }

    @Transactional
    public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest updateDto) {

        User currentUser = getCurrentUser();

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오를 찾을 수 없습니다. id=" + portfolioId));


        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access
                    .AccessDeniedException("해당 포트폴리오를 수정할 권한이 없습니다. id=" + portfolioId + " user=" + currentUser.getId());
        }

        portfolio.update(updateDto.getTitle(),updateDto.getAuthor(), updateDto.getCategory(), updateDto.getIsPublic());

    }

    public void deletePortfolioFile(Long fileId, Principal principal) {
        PortfolioFile fileToDelete = portfolioFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다. ID: " + fileId));

        Authentication authentication = (Authentication) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        String requestUserEmail = principal.getName();
        String ownerEmail = fileToDelete.getPortfolio().getUser().getEmail();

        // 관리자, 작성자 둘다 아닐경우 실행
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

    @Transactional
    public void deletePortfolio(Long portfolioId, Principal principal) {

        Portfolio portfolioToDelete = portfolioRepository.findByIdWithUserAndFiles(portfolioId)
                .orElseThrow(() -> new RuntimeException("해당 포트폴리오를 찾을 수 없습니다. ID: " + portfolioId));

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");
        if (!isAdmin && !portfolioToDelete.getUser().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("포트폴리오를 삭제할 권한이 없습니다.");
        }

        for (PortfolioFile file : portfolioToDelete.getFiles()) {
            try {
                Path filePath = Paths.get(file.getFileUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {

                log.error("포트폴리오 삭제 중 파일 시스템에서 파일 삭제 실패. Path: {}", file.getFileUrl(), e);
            }
        }

        portfolioRepository.delete(portfolioToDelete);

    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 DB에서 찾을 수 없습니다. (이메일: " + email + ")"));
    }

}