package it.epicode.travelsafebackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String nome;
    private String cognome;
    private String email;
    private String immagineProfilo;
    private String role;
    private String token;

    // Nuovi campi
    private String nickname;
    private String telefono;
    private String bio;


}
