package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.LoginRequestDTO;
import it.epicode.travelsafebackend.dto.RegisterResponseDTO;
import it.epicode.travelsafebackend.dto.RegisterUserDTO;
import it.epicode.travelsafebackend.dto.UserResponseDTO;
import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.exception.EmailAlreadyUsedException;
import it.epicode.travelsafebackend.repository.UserRepository;
import it.epicode.travelsafebackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponseDTO login(LoginRequestDTO loginRequest) {
        // normalizza l'email (trim per sicurezza)
        final String email = loginRequest.getEmail().trim();

        // 1) trova utente (ignore case)
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Credenziali non valide"));

        // 2) verifica password con encoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenziali non valide");
        }

        // 3) genera JWT
        String role = user.getRole().toString();
        String token = jwtUtils.generateToken(user.getEmail(), role);

        // 4) ritorna DTO
        return UserResponseDTO.builder()
                .nome(user.getNome())
                .cognome(user.getCognome())
                .email(user.getEmail())
                .immagineProfilo(user.getImmagineProfilo())
                .role(role)
                .token(token)
                .nickname(user.getNickname())
                .bio(user.getBio())
                .build();
    }

    public RegisterResponseDTO register(RegisterUserDTO request) {
        // normalizza email e ruolo
        final String email = request.getEmail().trim().toLowerCase();

        // validazione password
        if (request.getPassword() == null
                || request.getPassword().length() < 8
                || !request.getPassword().matches(".*[A-Z].*")
                || !request.getPassword().matches(".*[a-z].*")
                || !request.getPassword().matches(".*\\d.*")
                || !request.getPassword().matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException(
                    "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale");
        }

        // unicità email (case-insensitive)
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException("Questa email " + request.getEmail() + " è già registrata");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            log.warn("Ruolo non valido ricevuto: {}, uso default USER", request.getRole());
            role = Role.USER;
        }

        User newUser = User.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(email) // salva sempre lowercase
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(newUser);
        return new RegisterResponseDTO("Utente registrato con successo");
    }
}
