package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "citta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private double latitudine;
    private double longitudine;
    private String descrizione;
}