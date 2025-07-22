package it.epicode.travelsafebackend.repository;

import it.epicode.travelsafebackend.entity.ViaggioRegistrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViaggioRegistratoRepository extends JpaRepository<ViaggioRegistrato, Long> {
    List<ViaggioRegistrato> findByEmail(String email);
}
