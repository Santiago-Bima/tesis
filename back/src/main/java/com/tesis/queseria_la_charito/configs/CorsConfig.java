package com.tesis.queseria_la_charito.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

  /**
   * corsConfigurer.
   *
   * @return registry
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * addCorsMappings.
       *
       * @return registry
       */
      @Override
      public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // Puedes configurar los orígenes
            // permitidos según tus necesidades
            .allowedMethods("GET", "POST", "PUT", "DELETE");
        // Puedes configurar los métodos permitidos
      }
    };
  }
}

