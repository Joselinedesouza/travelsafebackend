package it.epicode.travelsafebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RecensioniRequestDTO {
    @NotNull
    private Long cittaId;


    private Long id;
    @NotBlank
    private String testo;

    @Min(1)
    @Max(5)
    private int voto;


}