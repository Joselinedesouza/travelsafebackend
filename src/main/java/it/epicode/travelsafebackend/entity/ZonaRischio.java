package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zona_rischio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZonaRischio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    private double latitudine;
    private double longitudine;

    @Enumerated(EnumType.STRING)
    private LivelloPericolo livelloPericolo;

    @ManyToOne
    @JoinColumn(name = "citta_id")
    private Citta citta;
}
