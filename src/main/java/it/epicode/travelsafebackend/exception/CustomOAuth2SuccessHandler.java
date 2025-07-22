package it.epicode.travelsafebackend.exception;

import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.UserRepository;
import it.epicode.travelsafebackend.security.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    private static final String FRONTEND_URL = "http://localhost:5173/home";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (email == null || email.isEmpty()) {
            throw new ServletException("Email non disponibile da OAuth2 provider");
        }
        if (name == null) {
            name = "";
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (!name.equals(user.getNome())) {
                user.setNome(name);
                userRepository.save(user);
            }
        } else {
            user = User.builder()
                    .email(email)
                    .nome(name)
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }

        String token = jwtUtils.generateToken(email, user.getRole().toString());

        // Costruisci URL redirect con token come parametro query
        String redirectUrl = FRONTEND_URL + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8.toString());

        // Fai redirect al frontend con il token in URL
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
