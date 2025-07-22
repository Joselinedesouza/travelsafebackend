package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.UserRepository;
import it.epicode.travelsafebackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class OAuth2CallbackController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @GetMapping("/oauth2/callback")
    public ResponseEntity<Void> handleOAuth2Callback(@RequestParam("code") String code,
                                                     @RequestParam("state") String state) {
        // Qui normalmente si scambia il code con il token (ma Spring Security lo fa internamente)
        // Se serve personalizzare, si può fare qui con OAuth2AuthorizedClientService

        // Recupera utente autenticato tramite email (esempio, potresti dover adattare)
        Optional<User> userOpt = userRepository.findByEmail(state); // usa state o altro identificatore

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOpt.get();

        // Genera JWT per l’utente
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        // Reindirizza verso frontend passando token (modifica URL frontend!)
        URI redirectUri = URI.create("http://localhost:5173/login/oauth2?token=" + token);

        return ResponseEntity.status(302).location(redirectUri).build();
    }
}
