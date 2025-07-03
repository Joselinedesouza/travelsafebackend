package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "punto_interesse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;
    private double latitudine;
    private double longitudine;

    @ManyToOne
    @JoinColumn(name = "citta_id")
    private Citta citta;
}
