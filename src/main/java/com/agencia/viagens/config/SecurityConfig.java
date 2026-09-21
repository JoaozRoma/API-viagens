package com.agencia.viagens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permite que o Spring produza respostas de erro coerentes.
                        .requestMatchers("/error").permitAll()

                        // Consultas de destinos são públicas.
                        .requestMatchers(HttpMethod.GET,
                                "/api/destinos",
                                "/api/destinos/**").permitAll()

                        // Usuários autenticados podem avaliar destinos.
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/destinos/*/avaliar").hasAnyRole("USER", "ADMIN")

                        // Somente administradores alteram o cadastro de destinos.
                        .requestMatchers(HttpMethod.POST,
                                "/api/destinos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/destinos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/destinos/*").hasRole("ADMIN")

                        // Proteção defensiva para qualquer operação futura
                        // não contemplada acima no recurso de destinos.
                        .requestMatchers("/api/destinos", "/api/destinos/**")
                        .hasRole("ADMIN")

                        // Gestão de usuários é exclusiva do administrador.
                        .requestMatchers("/api/usuarios", "/api/usuarios/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
