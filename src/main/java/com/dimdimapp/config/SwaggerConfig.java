package com.dimdimapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DimDimApp API")
                        .description("API RESTful para gerenciamento de clientes e transações - Projeto DimDim")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe DimDim")
                                .email("contato@dimdim.com")
                                .url("https://dimdim.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("https://dimdimappweb.azurewebsites.net")
                                .description("Servidor de Produção (Azure)"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local de Desenvolvimento")
                ));
    }
}
