package com.tesis.queseria_la_charito.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Value("${app.url}")
    private String url;

    @Value("${app.dev-name}")
    private String devName;

    @Value("${app.dev-email}")
    private String devEmail;

    @Bean
    public OpenAPI openApi(@Value("${app.name}") final String appName,
                           @Value("${app.desc}") final String appDescription,
                           @Value("${app.version}") final String appVersion) {

        Info info = new Info()
            .title(appName)
            .version(appVersion)
            .description(appDescription)
            .contact(new Contact()
                         .name(devName)
                         .email(devEmail));

        Server server = new Server()
            .url(url)
            .description(appDescription);

        // Configuración de Bearer Token
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

        // Añadir el esquema de seguridad en la configuración global
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
            .info(info)
            .addServersItem(server)
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public ModelResolver modelResolver(final ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }
}
