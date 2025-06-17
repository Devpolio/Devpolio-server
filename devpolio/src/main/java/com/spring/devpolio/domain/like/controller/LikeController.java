package com.spring.devpolio.domain.like.controller;

import com.spring.devpolio.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/{portfolioId}/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> addLike(@PathVariable Long portfolioId) {
        likeService.addLike(portfolioId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable Long portfolioId) {
        likeService.removeLike(portfolioId);
        return ResponseEntity.ok().build();
    }
}