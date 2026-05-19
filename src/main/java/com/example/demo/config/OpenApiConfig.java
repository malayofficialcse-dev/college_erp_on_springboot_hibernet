package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI collegeErpOpenApi() {
        return new OpenAPI().info(new Info()
                .title("College ERP API")
                .version("1.0.0")
                .description("Comprehensive backend API for academics, finance, HR, library, and communication modules.")
                .contact(new Contact().name("College ERP")));
    }
}
