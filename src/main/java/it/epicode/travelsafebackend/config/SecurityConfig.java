package it.epicode.travelsafebackend.config;

import it.epicode.travelsafebackend.exception.CustomOAuth2SuccessHandler;
import it.epicode.travelsafebackend.security.JwtFilter;
import it.epicode.travelsafebackend.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    // URL FE "principale" (utile se ti serve altrove)
    @Value("${app.frontend.base-url:https://frontend-travelsafe.vercel.app}")
    private String frontendBaseUrl;

    // Lista di origin consentiti per CORS, separati da virgola (dev + prod)
    // Esempio consigliato in application.properties:
    // cors.allowed-origins=https://frontend-travelsafe.vercel.app,http://localhost:5173
    @Value("${cors.allowed-origins:https://frontend-travelsafe.vercel.app,http://localhost:*}")
    private String corsAllowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Normalizza e applica gli origin (senza trailing slash)
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.replaceAll("/+$", "")) // rimuove "/" finale
                .collect(Collectors.toList());

        // Usa patterns per supportare wildcard tipo "http://localhost:*"
        config.setAllowedOriginPatterns(origins);

        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Location"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Pubblici
                        .requestMatchers("/", "/index", "/public/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // Tutta l'area auth è pubblica (login, register, reset, verify, resend, ecc.)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoint pubblici specifici (separati, solo per chiarezza)
                        .requestMatchers(HttpMethod.POST, "/api/location/save").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/citta/**", "/api/poi/**", "/api/zone-rischio/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Autenticati
                        .requestMatchers("/api/user/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/zone-rischio/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/zone-rischio/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").authenticated()
                        .requestMatchers("/api/viaggi/mine").authenticated()

                        // SOLO ADMIN (metti prima di regole generiche)
                        .requestMatchers(HttpMethod.DELETE, "/api/zone-rischio/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/notifiche").hasRole("ADMIN")

                        // Tutto il resto autenticato
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
