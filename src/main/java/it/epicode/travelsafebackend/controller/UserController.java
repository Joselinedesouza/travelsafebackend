package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.entity.Notifica;
import it.epicode.travelsafebackend.service.NotificaService;
import it.epicode.travelsafebackend.service.UserService;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NotificaService notificaService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id, @RequestBody MotivoRequest request) throws MessagingException {
        userService.deactivateUser(id, request.getMotivo());
        return ResponseEntity.ok("Utente disattivato e mail inviata.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Long id, @RequestBody MotivoRequest request) throws MessagingException {
        userService.activateUser(id, request.getMotivo());
        return ResponseEntity.ok("Utente attivato e mail inviata.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utente non trovato"));
        }
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Utente eliminato con successo."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Impossibile eliminare l'utente: " + e.getMessage()));
        }
    }

    // Endpoint per eliminare il profilo corrente (utente autenticato)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(@RequestBody(required = false) MotivoRequest request) {
        try {
            User currentUser = userService.getCurrentUser();

            // Se è stato inviato un motivo, salva la notifica
            if (request != null && request.getMotivo() != null && !request.getMotivo().trim().isEmpty()) {
                Notifica notifica = Notifica.builder()
                        .user(currentUser)
                        .messaggio("Utente ha richiesto eliminazione profilo. Motivo: " + request.getMotivo().trim())
                        .letta(false)
                        .build();
                notificaService.save(notifica);
            }

            userService.deleteUserWithMotivo(currentUser.getId(), request != null ? request.getMotivo() : null);

            return ResponseEntity.ok(Map.of("message", "Profilo eliminato con successo."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Impossibile eliminare il profilo: " + e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody User updatedUser) {
        User user = userService.updateCurrentUserProfile(updatedUser);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password aggiornata con successo."));
    }

    // DTO per la richiesta del motivo disattivazione/attivazione/eliminazione
    @Setter
    @Getter
    public static class MotivoRequest {
        private String motivo;
    }

    // DTO per cambio password
    @Setter
    @Getter
    public static class ChangePasswordRequest {
        private String newPassword;
    }
}
