package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.*;
import it.epicode.travelsafebackend.service.AuthService;
import it.epicode.travelsafebackend.service.PasswordService;
import it.epicode.travelsafebackend.service.EmailVerificationService;
import it.epicode.travelsafebackend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;

    // nuovi: per verifica email / reinvio token
    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        UserResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Errore di validazione");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterResponseDTO(errors));
        }

        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // --- Reset password ---

    @PostMapping("/request-reset")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO request) {
        passwordService.requestPasswordReset(request.getEmail());
        // risposta neutra per non rivelare se l'email esiste
        return ResponseEntity.ok("Se l'email è registrata, riceverai un link per reimpostare la password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordChangeDTO request) {
        passwordService.changePassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password cambiata con successo");
    }

    // --- Verifica email ---

    // L’utente clicca il link della mail -> FE chiama questo endpoint passando il token (via query string)
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        emailVerificationService.verify(token);
        return ResponseEntity.ok("Email verificata. Ora puoi accedere.");
    }

    // Opzionale: re-invio del token di verifica (risposta sempre 200 e neutra)
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam("email") String email) {
        userRepository.findByEmailIgnoreCase(email.trim())
                .filter(u -> !u.isEnabled())
                .ifPresent(emailVerificationService::sendVerificationEmail);
        return ResponseEntity.ok("Se l'account non era verificato, è stata inviata una nuova mail.");
    }
}
