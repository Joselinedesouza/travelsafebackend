package it.epicode.travelsafebackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CittaResponseDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private Double latitudine;
    private Double longitudine;
    private List<ZonaRischioResponseDTO> zoneRischio;
}
