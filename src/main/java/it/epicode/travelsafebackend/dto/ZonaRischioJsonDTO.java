package it.epicode.travelsafebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZonaRischioJsonDTO {
    private String nome;
    private String descrizione;
    private double latitudine;
    private double longitudine;
    private String livelloPericolo;
    private String nomeCitta;
}
