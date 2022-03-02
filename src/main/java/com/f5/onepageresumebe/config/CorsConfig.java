package com.f5.onepageresumebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://**");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("*"); // 응답을 보낼 때 헤더에 접근 가능
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}