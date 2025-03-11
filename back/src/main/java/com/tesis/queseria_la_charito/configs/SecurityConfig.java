package com.tesis.queseria_la_charito.configs;

import com.tesis.queseria_la_charito.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  private AuthenticationProvider authProvider;

//  TODO: Cambiar los requestMatchers
//  TODO: Ver como reemplazar el formulario de login por el de la app
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf ->
            csrf.disable())
        .authorizeHttpRequests(authRequest ->
                                   authRequest
                                       .requestMatchers(
                                           "/autenticacion/**",
                                           "/swagger-ui/**",
                                           "/v3/api-docs/**",
                                           "/swagger-ui.html",
                                           "/swagger-resources/**",
                                           "/webjars/**"
                                       )
                                       .permitAll()
                                       .anyRequest()
                                       .authenticated()
        )
        .sessionManagement(sessionManager ->
            sessionManager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

}
