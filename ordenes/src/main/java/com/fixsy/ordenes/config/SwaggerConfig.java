package com.fixsy.ordenes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fixsy Parts - API de Órdenes")
                        .version("1.0.0")
                        .description("API REST para gestión de órdenes de compra de Fixsy Parts")
                        .contact(new Contact()
                                .name("Fixsy Parts")
                                .email("soporte@fixsy.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

