package it.epicode.travelsafebackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecensioniResponseDTO {
    private Long id;
    private Long cittaId;
    private String cittaNome;
    private String testo;
    private int voto;
    private String dataCreazione;
    private String autoreEmail;
    private String risposta;
}