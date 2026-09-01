package com.meupolitico;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI meupoliticoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MeuPolítico API")
                        .description("API de transparência política — dados de parlamentares, despesas, votações, presença e patrimônio.")
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("MeuPolítico")
                                .email("emanuelsperandio@gmail.com"))
                        .license(new License()
                                .name("Uso acadêmico / TCC")));
    }
}