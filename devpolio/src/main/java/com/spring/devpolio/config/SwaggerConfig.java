package com.spring.devpolio.config;


import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi Authapi() {
        return GroupedOpenApi.builder()
                .group("auth") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/auth/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                                new Info()
                                                        .title("auth api") // API 제목
                                                        .description("로그인 회원가입을 처리하기 위한 API") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }
}
