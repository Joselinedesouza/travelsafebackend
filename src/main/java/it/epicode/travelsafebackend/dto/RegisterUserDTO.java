package it.epicode.travelsafebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {

    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    private String cognome;

    @NotBlank(message = "L'email non può essere vuota")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "La password non può essere vuota")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale"
    )
    private String password;

    private String role;

    private String immagineProfilo;

    private String nickname;

    private String telefono;

    private String bio;
}
