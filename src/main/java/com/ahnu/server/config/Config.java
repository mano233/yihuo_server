package com.ahnu.server.config;

import com.ahnu.server.helper.FastDFSHelper;
import org.csource.common.MyException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class Config implements WebMvcConfigurer {
    @Bean
    public FastDFSHelper fastDfs() throws MyException, IOException {
        return new FastDFSHelper("fdfs_client.conf");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                // .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

}
