package it.epicode.travelsafebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViaggioRegistratoDTO {
    private Long id;

    private String nome;
    private String cognome;
    private String email;

    private String nomeStrutturaAlloggio;

    private String motivoViaggio;

    private String telefonoCompleto;

    private String numeroEmergenza;



    private Double latitudine;
    private Double longitudine;

    private LocalDateTime dataRegistrazione;
}
