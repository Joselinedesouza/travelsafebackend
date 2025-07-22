package it.epicode.travelsafebackend.repository;


import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role); // Cambiato da findByRuolo a findByRole
}