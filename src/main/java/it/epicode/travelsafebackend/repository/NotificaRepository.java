package it.epicode.travelsafebackend.repository;

import it.epicode.travelsafebackend.entity.Notifica;
import it.epicode.travelsafebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepository extends JpaRepository<Notifica, Long> {
    List<Notifica> findByUser(User user);
}
