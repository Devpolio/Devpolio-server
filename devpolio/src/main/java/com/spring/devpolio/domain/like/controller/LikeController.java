package com.spring.devpolio.domain.like.controller;

import com.spring.devpolio.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/{id}")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("like")
    public ResponseEntity<String> addLike(@PathVariable Long id) {
        String result = likeService.addLike(id);

        return "ALREADY_LIKED".equals(result)
                ? ResponseEntity.status(HttpStatus.CONFLICT).body("이미 '좋아요'를 누른 포트폴리오입니다.")
                : ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    @DeleteMapping("/like")
    public ResponseEntity<String> removeLike(@PathVariable Long id) {
        String result = likeService.removeLike(id);

        return "NOT_LIKED".equals(result)
                ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("'좋아요'를 누른 기록이 없습니다.")
                : ResponseEntity.ok("좋아요가 취소되었습니다.");
    }
}