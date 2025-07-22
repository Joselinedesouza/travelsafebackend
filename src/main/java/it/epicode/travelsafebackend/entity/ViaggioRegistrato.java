package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "viaggio_registrato")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViaggioRegistrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String email;

    private String nomeStrutturaAlloggio; // Indirizzo alloggio

    private String motivoViaggio;

    private String telefonoCompleto;

    private String numeroEmergenza;


    private Double latitudine;
    private Double longitudine;

    private LocalDateTime dataRegistrazione;
}
