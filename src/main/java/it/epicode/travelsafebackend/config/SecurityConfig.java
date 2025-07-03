package it.epicode.travelsafebackend.config;

import it.epicode.travelsafebackend.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // Permette l'uso di @PreAuthorize nei controller
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        //  accesso pubblico a tutte le rotte /api/auth/**
                        .requestMatchers("/api/auth/**").permitAll()

                        // GET pubblici (anche per mappe)
                        .requestMatchers(HttpMethod.GET, "/api/citta/**", "/api/poi/**", "/api/zone-rischio/**").permitAll()

                        // USER/ADMIN possono creare/modificare zone
                        .requestMatchers(HttpMethod.POST, "/api/zone-rischio/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/zone-rischio/**").authenticated()

                        // Solo ADMIN può gestire utenti e cancellare zone
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/zone-rischio/**").hasRole("ADMIN")

                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .oauth2Login(oauth -> oauth.disable());

        return http.build();
    }

    // Utile se ti serve AuthenticationManager per l’AuthService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
