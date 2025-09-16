package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.PasswordResetToken;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.exception.BadRequestException;
import it.epicode.travelsafebackend.repository.PasswordResetTokenRepository;
import it.epicode.travelsafebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.password-reset.token-hours:1}")
    private int tokenValidityHours;

    // usa il mittente dalle properties; fallback allo username SMTP
    @Value("${spring.mail.from:${spring.mail.username:}}")
    private String mailFrom;

    /**
     * Richiesta reset: non rivelare se l'email esiste.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        final String normalized = Optional.ofNullable(email).orElse("").trim();

        Optional<User> maybeUser = userRepository.findByEmailIgnoreCase(normalized);
        if (maybeUser.isEmpty()) {
            log.info("Password reset richiesto per email non registrata: {}", normalized);
            return; // sempre 200 dal controller
        }

        User user = maybeUser.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(tokenValidityHours);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiry);
        tokenRepository.save(resetToken);

        String link = buildResetLink(token);
        log.info("Generato reset link per {}: {}", user.getEmail(), link);

        sendResetEmail(user.getEmail(), link);
    }

    /**
     * Cambio password con token.
     */
    @Transactional
    public void changePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token non valido o scaduto"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("Token scaduto");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // invalida il token dopo l'uso
        log.info("Password aggiornata per utente {}", user.getEmail());
    }

    private String buildResetLink(String token) {
        String base = frontendBaseUrl != null ? frontendBaseUrl.trim().replaceAll("/+$", "") : "http://localhost:5173";

        // In locale puoi scegliere:
        // - se usi HashRouter: usa "/#/reset-password"
        // - se usi BrowserRouter con fallback (vite/rewrites): usa "/reset-password"
        boolean isLocal = base.contains("localhost") || base.contains("127.0.0.1");
        String path = isLocal ? "/reset-password" : "/reset-password"; // cambia in "/#/reset-password" se preferisci HashRouter in dev

        return UriComponentsBuilder.fromHttpUrl(base)
                .path(path)
                .queryParam("token", token)
                .toUriString();
    }

    private void sendResetEmail(String to, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (mailFrom != null && !mailFrom.isBlank()) {
                message.setFrom(mailFrom);
            }
            message.setTo(to);
            message.setSubject("Recupero Password • TravelSafe");
            message.setText(
                    "Ciao,\n\n" +
                            "Per reimpostare la tua password clicca il link qui sotto (valido per " + tokenValidityHours + " ora/e):\n" +
                            resetLink + "\n\n" +
                            "Se non hai richiesto questa operazione, puoi ignorare questa email.\n\n" +
                            "— TravelSafe Team"
            );
            mailSender.send(message);
            log.info("Email reset inviata a {}", to);
        } catch (Exception ex) {
            // Non bloccare il flusso: logga per diagnosi
            log.error("Invio email reset fallito verso {}: {}", to, ex.getMessage(), ex);
        }
    }
}
