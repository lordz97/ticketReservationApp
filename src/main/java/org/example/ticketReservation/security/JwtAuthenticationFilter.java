package org.example.ticketReservation.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. On récupère l'en-tête (Header) "Authorization" de la requête HTTP
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Si l'en-tête est vide ou ne commence pas par "Bearer ", ce n'est pas un token JWT.
        // On laisse passer la requête (Spring Security la bloquera plus tard si elle est protégée).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. On extrait le token (on coupe les 7 premiers caractères : "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 4. On extrait l'email depuis notre JWT via le JwtService
            userEmail = jwtService.extractUsername(jwt);

            // 5. Si on a bien un email ET que l'utilisateur n'est pas déjà connecté dans le contexte actuel
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // On charge les infos de l'utilisateur depuis la base de données
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. On demande au JwtService si le token est valide (bonne signature, pas expiré)
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // Si oui, on crée un objet d'authentification officiel pour Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // On y attache les détails de la requête HTTP (ex: adresse IP)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 7. On met à jour le contexte de sécurité : l'utilisateur est officiellement connecté !
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Warning: Attempt to connect with an expired token.");
        } catch (JwtException e) {
            System.out.println("Warning: Attempt to connect with an invalid or corrupted token.");
        }
        // On passe le relais au filtre suivant
        filterChain.doFilter(request, response);
    }
}