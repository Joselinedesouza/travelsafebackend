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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Login fallito per email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Credenziali non valide");
        } catch (Exception ex) {
            log.error("Errore durante login per email {}: {}", loginRequest.getEmail(), ex.getMessage());
            throw new RuntimeException("Errore durante l'autenticazione");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        String role = user.getRole().toString();
        String token = jwtUtils.generateToken(user.getEmail(), role);

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
        // Validazione password
        if (request.getPassword() == null || request.getPassword().length() < 8
                || !request.getPassword().matches(".*[A-Z].*")
                || !request.getPassword().matches(".*[a-z].*")
                || !request.getPassword().matches(".*\\d.*")
                || !request.getPassword().matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException(
                    "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
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
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(newUser);
        return new RegisterResponseDTO("Utente registrato con successo");
    }
}
