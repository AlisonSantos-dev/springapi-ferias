package com.alisonsantos.springapiferias.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    // Lista de origens liberadas no CORS, separadas por virgula.
    // O padrao abaixo ja inclui a URL de producao (http e https), para
    // funcionar mesmo que o perfil "prod" nao esteja ativo no container.
    // Pode ser sobrescrito pela propriedade app.cors.allowed-origins
    // ou pela variavel de ambiente CORS_ALLOWED_ORIGINS.
    @Value("${app.cors.allowed-origins:${CORS_ALLOWED_ORIGINS:"
            + "http://localhost:3000,"
            + "http://localhost:5173,"
            + "http://127.0.0.1:5500,"
            + "https://springapi-ferias.apps.db1group.com,"
            + "http://springapi-ferias.apps.db1group.com}}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // sem token (ou com token invalido/expirado) a resposta e 401, e nao
                // 403. Assim o front distingue "precisa entrar de novo" (401) de
                // "entrou, mas nao tem permissao pra isso" (403).
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // API publica
                        .requestMatchers("/api/login", "/h2-console/**", "/error", "/health").permitAll()
                        // login pela conta DB1: quem autentica aqui e o gateway do portal
                        // (headers X-DB1-*), nao o nosso JWT - ver SsoController
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/colaboradores/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/equipes").permitAll()
                        // paginas e arquivos do front (React) - o SecurityFilter nunca
                        // encontra token aqui, entao precisam ficar liberados
                        .requestMatchers(
                                "/", "/index.html", "/favicon.svg", "/assets/**",
                                "/login", "/cadastro", "/trocar-senha", "/minhas-ferias", "/coordenador"
                        ).permitAll()
                        // qualquer outra coisa embaixo de /api exige token valido
                        .anyRequest().authenticated())
                // necessario para o console do H2 conseguir renderizar dentro de um frame
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens vindas da propriedade app.cors.allowed-origins.
        // Em producao precisa conter https://springapi-ferias.apps.db1group.com,
        // senao o navegador manda o header Origin no POST /api/login e o Spring
        // responde 403 "Invalid CORS request".
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
