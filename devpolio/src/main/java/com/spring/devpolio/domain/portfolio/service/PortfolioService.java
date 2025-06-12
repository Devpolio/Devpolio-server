    package com.spring.devpolio.domain.portfolio.service;


    import com.spring.devpolio.domain.portfolio.dto.addPortfolioDto;
    import com.spring.devpolio.domain.portfolio.dto.addPortfolioResponseDto;
    import com.spring.devpolio.domain.portfolio.entity.Portfolio;
    import com.spring.devpolio.domain.portfolio.entity.PortfolioFile;
    import com.spring.devpolio.domain.portfolio.repository.PortfolioRepository;
    import com.spring.devpolio.domain.user.entity.User;
    import com.spring.devpolio.domain.user.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.apache.coyote.Response;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.security.Principal;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;


    @RequiredArgsConstructor
    @Service
    public class PortfolioService {
        private final PortfolioRepository portfolioRepository;
        private final UserRepository userRepository;

        @Value("${file.upload-dir}")
        private String uploadDirectory;

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
            portfolio.setLikes(0);

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
    }