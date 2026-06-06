package cl.duoc.ms_usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
        OpenAPI customOpenAPI(){

            return new OpenAPI()
                .info(new Info()
                .title("Microservicio Usuarios")
                .version("1.0")
                .description("Gestión de Usuarios")
                                     
            );        
        }
}