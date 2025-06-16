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

    @Bean
    public GroupedOpenApi PortfolioApi() {
        return GroupedOpenApi.builder()
                .group("portfolio")
                .pathsToMatch("/portfolio/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("portfolio api")
                                                .description("포트폴리오 CRUD API")
                                                .version("1.0.0")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("admin api")
                                                .description("어드민만 접근 가능합니다")
                                                .version("1.0.0")
                                )
                ).build();
    }

    @Bean
    public GroupedOpenApi FileApi() {
        return GroupedOpenApi.builder()
                .group("file")
                .pathsToMatch("/files/**")
                .addOpenApiCustomizer(
                openApi -> openApi.setInfo(
                        new Info()
                                .title("file api")
                                .description("파일 처리, 다운로드 api")
                                .version("1.0.0")
                )
        ).build();
    }

    @Bean
    public GroupedOpenApi UserApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("user api")
                                                .description("사용자 정보 확인용 api")
                                                .version("1.0.0")
                                )
                )
                .build();
    }


}
