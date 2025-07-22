package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.PasswordResetToken;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.exception.BadRequestException;
import it.epicode.travelsafebackend.exception.UserNotFoundException;
import it.epicode.travelsafebackend.repository.PasswordResetTokenRepository;
import it.epicode.travelsafebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository; // repository per salvare token reset

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email non trovata"));

        String token = UUID.randomUUID().toString();

        // Salva token con riferimento all’utente e scadenza (es. 1 ora)
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        // Invia email con link tipo: http://localhost:8080/reset-password?token=xyz
        sendResetEmail(user.getEmail(), token);
    }

    public void changePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token non valido o scaduto"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token scaduto");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Opzionale: elimina il token dopo l’uso
        tokenRepository.delete(resetToken);
    }

    private void sendResetEmail(String email, String token) {
        String link = "http://localhost:5173/reset-password?token=" + token; // frontend URL per reset password
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recupero Password TravelSafe");
        message.setText("Ciao,\n\nClicca il link sottostante per impostare una nuova password:\n" + link +
                "\n\nSe non hai richiesto questa operazione, ignora questa email.\n\nSaluti,\nTravelSafe Team");
        mailSender.send(message);
    }
}
