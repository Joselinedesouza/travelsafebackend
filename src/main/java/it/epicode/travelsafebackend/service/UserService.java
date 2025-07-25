package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.UserResponseDTO;
import it.epicode.travelsafebackend.entity.Notifica;
import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.PasswordResetTokenRepository;
import it.epicode.travelsafebackend.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final NotificaService notificaService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        return UserResponseDTO.builder()
                .nome(user.getNome())
                .cognome(user.getCognome())
                .email(user.getEmail())
                .immagineProfilo(user.getImmagineProfilo())
                .role(user.getRole().toString())
                .token(null)
                .nickname(user.getNickname())
                .bio(user.getBio())
                .build();
    }

    public UserResponseDTO updateUserProfile(String email, String nickname, String telefono, String bio, MultipartFile immagineProfilo) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (nickname != null) user.setNickname(nickname);
        if (telefono != null) user.setTelefono(telefono);
        if (bio != null) user.setBio(bio);

        if (immagineProfilo != null && !immagineProfilo.isEmpty()) {
            try {
                System.out.println("➡️ Ricevuto file: " + immagineProfilo.getOriginalFilename());
                String imageUrl = storageService.storeFile(immagineProfilo);
                user.setImmagineProfilo(imageUrl);
            } catch (Exception e) {
                System.err.println("❌ Errore durante upload immagine profilo: " + e.getMessage());

            }
        } else {
            System.out.println("⚠️ Nessun file immagine ricevuto.");
        }

        userRepository.save(user);

        return UserResponseDTO.builder()
                .nome(user.getNome())
                .cognome(user.getCognome())
                .email(user.getEmail())
                .immagineProfilo(user.getImmagineProfilo())
                .role(user.getRole().toString())
                .token(null)
                .nickname(user.getNickname())
                .bio(user.getBio())
                .build();
    }

    public void deactivateUser(Long id, String motivo) throws MessagingException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        user.setEnabled(false);
        userRepository.save(user);

        String subject = "Account disattivato";
        String text = String.format("Ciao %s,\n\nIl tuo account è stato disattivato per il seguente motivo:\n%s\n\nContatta l'amministrazione per maggiori informazioni.", user.getNome(), motivo);
        emailService.sendEmail(user.getEmail(), subject, text);
    }

    public void activateUser(Long id, String motivo) throws MessagingException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        user.setEnabled(true);
        userRepository.save(user);

        String subject = "Account riattivato";
        String text = String.format("Ciao %s,\n\nIl tuo account è stato riattivato.\nMessaggio dall'amministrazione:\n%s\n\nBentornato! Saluti TravelSafeTeam", user.getNome(), motivo);
        emailService.sendEmail(user.getEmail(), subject, text);
    }

    /**
     * Elimina un utente per id con motivo e notifica tutti gli admin.
     * Per la notifica realtime, è possibile integrare WebSocket nel NotificaService.
     */
    @Transactional
    public void deleteUserWithMotivo(Long id, String motivo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Passa direttamente l'enum Role.ADMIN
        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);

        for (User admin : adminUsers) {
            Notifica notifica = new Notifica();
            notifica.setUser(admin);
            notifica.setMessaggio("L'utente " + user.getEmail() + " ha eliminato il proprio profilo. Motivo: " + motivo);
            notifica.setTimestamp(LocalDateTime.now());
            notificaService.save(notifica);
        }

        passwordResetTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato");
        }
        passwordResetTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));
    }

    public User updateCurrentUserProfile(User updatedUser) {
        User currentUser = getCurrentUser();

        if (updatedUser.getNickname() != null) currentUser.setNickname(updatedUser.getNickname());
        if (updatedUser.getTelefono() != null) currentUser.setTelefono(updatedUser.getTelefono());
        if (updatedUser.getBio() != null) currentUser.setBio(updatedUser.getBio());

        return userRepository.save(currentUser);
    }

    public void changePassword(String newPassword) {
        User currentUser = getCurrentUser();
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
