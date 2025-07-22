package it.epicode.travelsafebackend.dto;

import it.epicode.travelsafebackend.entity.LivelloPericolo;
import it.epicode.travelsafebackend.validation.ValidLatitudine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ZonaRischioRequestDTO {

    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    @NotNull(message = "La latitudine è obbligatoria")
    @ValidLatitudine
    private Double latitudine;

    @NotNull(message = "La longitudine è obbligatoria")
    private Double longitudine;

    @NotNull(message = "Il livello di pericolo è obbligatorio")
    private LivelloPericolo livelloPericolo;

    private Long cittaId;     // id se città esistente

    private String nomeCitta; // nome della città (per creare una nuova città se cittaId è null)
}
