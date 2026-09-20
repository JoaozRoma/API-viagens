package com.agencia.viagens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de consulta de destinos
                .requestMatchers(HttpMethod.GET, "/api/destinos/**").permitAll()
                
                // Avaliação de destinos permitida para perfis USER e ADMIN
                .requestMatchers(HttpMethod.PATCH, "/api/destinos/{id}/avaliar").hasAnyRole("USER", "ADMIN")
                
                // Operações restritas a administradores (Cadastro, Atualização, Exclusão de destinos)
                .requestMatchers(HttpMethod.POST, "/api/destinos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/destinos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/destinos/**").hasRole("ADMIN")
                
                // Gestão de usuários restrita a administradores
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                
                // Qualquer outra requisição deve estar autenticada
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
