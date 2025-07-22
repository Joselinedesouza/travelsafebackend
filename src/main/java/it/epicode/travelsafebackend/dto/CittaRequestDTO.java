package it.epicode.travelsafebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CittaRequestDTO {
    @NotNull
    private String nome;

    @NotNull
    private String descrizione;


    @NotNull
    private Double latitudine;

    @NotNull
    private Double longitudine;
}
