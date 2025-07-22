package it.epicode.travelsafebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReplyRequestDTO {
    @NotBlank(message = "Il testo della risposta è obbligatorio")
    private String testoRisposta;
}
