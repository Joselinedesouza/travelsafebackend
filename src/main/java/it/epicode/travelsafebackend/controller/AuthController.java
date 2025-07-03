package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.AuthResponseDTO;
import it.epicode.travelsafebackend.dto.LoginRequestDTO;
import it.epicode.travelsafebackend.dto.RegisterResponseDTO;
import it.epicode.travelsafebackend.dto.RegisterUserDTO;
import it.epicode.travelsafebackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterUserDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest);
    }
}
