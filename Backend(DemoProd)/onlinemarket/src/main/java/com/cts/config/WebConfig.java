package com.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://online-marketplace-bucket.s3-website-us-east-1.amazonaws.com",
                        		"https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd",
                        		"http://www.gencpod-omp.com","http://gencpod-omp.com",
                        		"http://www.gencpod-omp.com.s3-website-us-east-1.amazonaws.com",
                        		"127.0.0.1:3000",
                        		"44.208.268.167:9090")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}