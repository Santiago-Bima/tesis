package com.tesis.queseria_la_charito.configs;

import com.tesis.queseria_la_charito.JwtAuthenticationFilter;
import com.tesis.queseria_la_charito.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  private AuthenticationProvider authProvider;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf ->
            csrf.disable())
        .authorizeHttpRequests(authRequest ->
                                   authRequest
                                       .requestMatchers(
                                           "/autenticacion/login",
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

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(customUserDetailsService);
    return authenticationManagerBuilder.build();
  }

}
