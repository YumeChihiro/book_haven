package com.haven.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
	   @Bean
	    public OpenAPI customOpenAPI() {
	        return new OpenAPI()
	            .info(new Info().title("BookHaven API").version("1.0"))
	            // 1. Định nghĩa cách gửi token
	            .components(new Components()
	                .addSecuritySchemes("BearerAuth", 
	                    new SecurityScheme()
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme("bearer")
	                        .bearerFormat("JWT")
	                        .name("Authorization") // Khớp với tên header trong backend
	                )
	            )
	            // 2. Áp dụng cho tất cả API
	            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
	    }
}
