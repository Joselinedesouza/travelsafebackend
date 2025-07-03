package it.epicode.travelsafebackend.dto;

import it.epicode.travelsafebackend.entity.LivelloPericolo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZonaRischioResponseDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private double latitudine;
    private double longitudine;
    private LivelloPericolo livelloPericolo;
    private String nomeCitta;
}
