package it.epicode.travelsafebackend.repository;

import it.epicode.travelsafebackend.entity.Recensione;
import it.epicode.travelsafebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {
    List<Recensione> findByUser(User user);
}
