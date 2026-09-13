package com.example.backend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addServersItem(
                Server()
                    .url("http://localhost:8081")
                    .description("Servidor local de desenvolvimento")
            )
            .info(
                Info()
                    .title("AquaMonitor - API REST")
                    .version("0.0.1-SNAPSHOT")
                    .description(
                        "Sistema de gerenciamento e monitoramento de lojas de aquarismo. " +
                            "Inclui CRUD de lojas, aquários, espécies, espécimes, compradores, vendas, alertas, " +
                            "dispositivos IoT, configurações de temperatura, programações de alimentação e " +
                            "recebimento de leituras de temperatura via integração MQTT com ESP32."
                    )
                    .contact(
                        Contact()
                            .name("Projeto Integrador 6º Semestre")
                            .url("https://github.com/")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
    }
}
