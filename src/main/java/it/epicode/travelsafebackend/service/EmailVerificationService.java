package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.EmailVerificationToken; // l'entity appena creata
import it.epicode.travelsafebackend.entity.User;                   // l'utente da verificare
import it.epicode.travelsafebackend.exception.BadRequestException; // eccezione 400
import it.epicode.travelsafebackend.repository.EmailVerificationTokenRepository; // repo token
import it.epicode.travelsafebackend.repository.UserRepository;                    // repo utente
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;           // per leggere properties
import org.springframework.mail.SimpleMailMessage;                  // email semplice
import org.springframework.mail.javamail.JavaMailSender;           // sender mail
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;     // transazioni
import org.springframework.web.util.UriComponentsBuilder;           // costruzione URL

import java.time.LocalDateTime;
import java.util.UUID;

@Service                        // marca come service Spring
@RequiredArgsConstructor        // Lombok: genera costruttore con i final
@Slf4j                          // logger
public class EmailVerificationService {

    // Dipendenze: repo token, repo user, mail sender
    private final EmailVerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final JavaMailSender mailSender;

    // URL del frontend (per costruire il link di verifica)
    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    // Validità del token (in ore), con default 24h
    @Value("${app.email-verification.token-hours:24}")
    private int tokenHours;

    // Mittente: prende spring.mail.from (o username) dalle properties
    @Value("${spring.mail.from:${spring.mail.username:}}")
    private String mailFrom;

    // Invia l'email di verifica a un utente appena registrato (o su reinvio)
    @Transactional
    public void sendVerificationEmail(User user) {
        // Per sicurezza: rimuove un token precedente dell’utente
        tokenRepo.deleteByUserId(user.getId());

        // Genera un token random
        String token = UUID.randomUUID().toString();

        // Crea e salva a DB il token con scadenza
        var evt = new EmailVerificationToken(null, token, user,
                LocalDateTime.now().plusHours(tokenHours));
        tokenRepo.save(evt);

        // Costruisce il link di verifica che aprirà il frontend
        String link = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl.replaceAll("/+$",""))
                .path("/verify-email")
                .queryParam("token", token)
                .toUriString();

        // Prova a inviare la mail (se fallisce, logga ma non blocca il flusso)
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (mailFrom != null && !mailFrom.isBlank()) msg.setFrom(mailFrom);
            msg.setTo(user.getEmail());
            msg.setSubject("Verifica la tua email • TravelSafe");
            msg.setText(
                    "Ciao" + (user.getNome() != null ? user.getNome() : "") + ",\n\n" + "sei ad un passo dal completare la tua registrazione" +
                            "per proseguire conferma il tuo indirizzo email cliccando qui (valido per " + tokenHours + "h):\n" +
                            link + "\n\nSiamo lieti di averti tra di noi,perchè la tua sicurezza è il nostro viaggio\n— TravelSafe Team"
            );
            mailSender.send(msg);
            log.info("Inviata email di verifica a {}", user.getEmail());
        } catch (Exception e) {
            log.error("Invio mail verifica fallito verso {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    // Valida il token cliccato dall’utente e abilita l’account
    @Transactional
    public void verify(String token) {
        // Recupera il token (se non esiste → 400)
        var evt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token non valido"));

        // Se scaduto: elimina e segnala
        if (evt.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(evt);
            throw new BadRequestException("Token scaduto");
        }

        // Marca l'utente come "enabled" = verificato
        var user = evt.getUser();
        user.setEnabled(true);
        userRepo.save(user);

        // Token usa-e-getta: lo rimuoviamo
        tokenRepo.delete(evt);

        log.info("Email verificata per {}", user.getEmail());
    }
}
