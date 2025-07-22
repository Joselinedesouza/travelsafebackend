package it.epicode.travelsafebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeDTO {

    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;

}
