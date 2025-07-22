package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Estrai info da Google
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("given_name");
        String cognome = oAuth2User.getAttribute("family_name");

        // Gestione valori nulli per evitare NullPointerException
        final String emailFinal = email != null ? email : "";
        final String nomeFinal = nome != null ? nome : "";
        final String cognomeFinal = cognome != null ? cognome : "";

        // Controllo se email è vuota o nulla, interrompe autenticazione
        if (emailFinal.isEmpty()) {
            throw new OAuth2AuthenticationException("Email non disponibile da OAuth2 provider");
        }

        // Cerca utente o crealo
        User user = userRepository.findByEmail(emailFinal).orElseGet(() -> {
            User newUser = User.builder()
                    .email(emailFinal)
                    .nome(nomeFinal)
                    .cognome(cognomeFinal)
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            return userRepository.save(newUser);
        });

        // Aggiorna nome/cognome se cambiati (opzionale)
        boolean updated = false;
        if (!nomeFinal.equals(user.getNome())) {
            user.setNome(nomeFinal);
            updated = true;
        }
        if (!cognomeFinal.equals(user.getCognome())) {
            user.setCognome(cognomeFinal);
            updated = true;
        }
        if (updated) {
            userRepository.save(user);
        }

        // Mappa i ruoli Spring Security (es. ROLE_USER)
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Restituisci DefaultOAuth2User con ruoli e attributi Google
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email"  // key username/email
        );
    }
}
