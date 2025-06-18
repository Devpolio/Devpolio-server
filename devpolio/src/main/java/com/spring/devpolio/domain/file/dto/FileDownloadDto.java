package com.spring.devpolio.domain.file.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

@Getter
@RequiredArgsConstructor
public class FileDownloadDto {
    private final Resource resource;
    private final String originalFileName;
}