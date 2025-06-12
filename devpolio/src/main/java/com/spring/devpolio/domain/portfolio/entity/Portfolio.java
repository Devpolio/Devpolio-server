package com.spring.devpolio.domain.portfolio.entity;

import com.spring.devpolio.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private LocalDateTime createdAt;

    private String password;

    private String category;

    private Boolean isPublic;

    private int likes;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioFile> files = new ArrayList<>();

    @ManyToOne
    private User user;

    @Transient
    private List<MultipartFile> portfolioFiles; // 파일 업로드 용도
}

