package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.UserResponseDTO;
import it.epicode.travelsafebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Principal principal) {
        String email = principal.getName();

        UserResponseDTO dto = userService.getUserProfile(email);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestParam String nickname,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) MultipartFile immagineProfilo,
            Principal principal
    ) throws IOException {
        String email = principal.getName();

        UserResponseDTO updatedUser = userService.updateUserProfile(email, nickname, telefono, bio, immagineProfilo);

        return ResponseEntity.ok(updatedUser);
    }
}
