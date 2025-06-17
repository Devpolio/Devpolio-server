package com.spring.devpolio.domain.portfolio.entity;

import com.spring.devpolio.domain.like.entity.Like;
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


    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioFile> files = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Transient
    private List<MultipartFile> portfolioFiles; // 파일 업로드 용도

    public int getLikeCount() {
        return this.likes.size();
    }
}

