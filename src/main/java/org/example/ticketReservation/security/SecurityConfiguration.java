package org.example.ticketReservation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'activer la sécurité fine sur les méthodes si besoin
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // 1. Le hacheur de mots de passe (L'algorithme industriel BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Le fournisseur d'authentification (Relie la BDD et le hacheur)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Le gestionnaire d'authentification principal
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. LE CERVEAU : La chaîne de filtres (Syntaxe Lambda DSL moderne)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation du CSRF (Totalement inutile pour une API REST Stateless avec JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Configuration des routes (RBAC : Role-Based Access Control)
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques : Inscription, Login, et Swagger (Documentation)
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Routes administrateur : Seul l'ADMIN peut créer ou modifier des ressources
                        .requestMatchers(HttpMethod.POST, "/api/resources").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasRole("ADMIN")

                        // Routes communes : Tout le monde peut voir le catalogue (GET) ou réserver
                        // Toutes les autres requêtes nécessitent juste un JWT valide
                        .anyRequest().authenticated()
                )

                // Mode Stateless : On interdit formellement à Spring de créer des sessions (Cookies)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // On branche notre mécanique d'authentification personnalisée
                .authenticationProvider(authenticationProvider())

                // On place notre Vigile JWT *AVANT* le filtre classique de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // FINITIONS PRO : Gestion des exceptions de sécurité pour éviter les crashs 500
                .exceptionHandling(exceptions -> exceptions
                        // Utilisateur non connecté qui tente d'accéder à une route privée (401)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"erreur\": \"Non autorise. Jeton manquant ou invalide.\"}");
                        })
                        // Utilisateur connecté mais qui n'a pas le droit (ex: USER qui veut poster une ressource) (403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"erreur\": \"Acces refuse. Vous devez etre ADMIN.\"}");
                        })
                );

        return http.build();
    }
}