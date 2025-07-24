package it.epicode.travelsafebackend.repository;


import it.epicode.travelsafebackend.entity.Citta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CittaRepository extends JpaRepository<Citta, Long> {
    Optional<Citta> findByNomeIgnoreCase(String nome);
}