package it.epicode.travelsafebackend.dto;

import it.epicode.travelsafebackend.entity.LivelloPericolo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZonaPerCittaStatDTO {
    private String nomeCitta;
    private Long totaleZone;
    private LivelloPericolo livelloPericolo;
}