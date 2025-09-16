package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.*;
import it.epicode.travelsafebackend.service.AuthService;
import it.epicode.travelsafebackend.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;


    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        UserResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Errore di validazione");

            RegisterResponseDTO errorResponse = new RegisterResponseDTO(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO request) {
        passwordService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Se l'email è registrata, riceverai un link per reimpostare la password.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordChangeDTO request) {
        passwordService.changePassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password cambiata con successo");
    }

}
