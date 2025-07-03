package it.epicode.travelsafebackend.repository;


import it.epicode.travelsafebackend.entity.Citta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CittaRepository extends JpaRepository<Citta, Long> {}