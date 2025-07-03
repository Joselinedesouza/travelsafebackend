package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.*;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.repository.UserRepository;
import it.epicode.travelsafebackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        String role = user.getRole().toString(); // o come recuperi il ruolo dell'utente
        String token = jwtUtils.generateToken(user.getEmail(), role);
        return new AuthResponseDTO(token);
    }

    public RegisterResponseDTO register(RegisterUserDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        Role role;
        try{
            role=Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e){
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
