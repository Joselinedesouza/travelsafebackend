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

    // Per inviare il token di verifica all’utente appena registrato
    private final EmailVerificationService emailVerificationService;

    /**
     * Login:
     * - cerca utente per email (case-insensitive)
     * - verifica password con PasswordEncoder
     * - blocca accesso se l’email non è stata verificata (enabled=false)
     * - genera e ritorna JWT
     */
    public UserResponseDTO login(LoginRequestDTO loginRequest) {
        final String email = loginRequest.getEmail().trim();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Credenziali non valide"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenziali non valide");
        }

        if (!user.isEnabled()) {
            // messaggio chiaro per il FE
            throw new BadCredentialsException("Account non verificato. Controlla la mail.");
        }

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

    /**
     * Register:
     * - normalizza l’email a lowercase
     * - valida la password
     * - verifica unicità email (case-insensitive)
     * - determina il ruolo (fallback USER)
     * - salva utente con enabled=false
     * - invia email di verifica con token
     */
    public RegisterResponseDTO register(RegisterUserDTO request) {
        final String email = request.getEmail().trim().toLowerCase();

        // Validazione password (come avevi già)
        if (request.getPassword() == null
                || request.getPassword().length() < 8
                || !request.getPassword().matches(".*[A-Z].*")
                || !request.getPassword().matches(".*[a-z].*")
                || !request.getPassword().matches(".*\\d.*")
                || !request.getPassword().matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException(
                    "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale");
        }

        // Unicità email (case-insensitive)
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException("Questa email " + request.getEmail() + " è già registrata");
        }

        // Parsing ruolo con fallback a USER
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            log.warn("Ruolo non valido ricevuto: {}, uso default USER", request.getRole());
            role = Role.USER;
        }

        // Creazione utente: ENABLED = false finché non verifica l’email
        User newUser = User.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(email) // salva sempre lowercase
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(false) // <--- importante per la verifica email
                .build();

        userRepository.save(newUser);

        // Invio mail di verifica (token + link)
        emailVerificationService.sendVerificationEmail(newUser);

        return new RegisterResponseDTO("Registrazione completata! Controlla la mail per verificare l'account.");
    }
}
