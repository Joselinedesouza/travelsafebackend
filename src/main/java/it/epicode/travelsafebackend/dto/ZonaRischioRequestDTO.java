package it.epicode.travelsafebackend.dto;

import it.epicode.travelsafebackend.entity.LivelloPericolo;
import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class ZonaRischioRequestDTO {
    @NotBlank
    private String nome;

    @NotBlank
    private String descrizione;

    @NotNull
    private Double latitudine;

    @NotNull
    private Double longitudine;

    @NotNull
    private LivelloPericolo livelloPericolo;

    @NotNull
    private Long cittaId;
}
