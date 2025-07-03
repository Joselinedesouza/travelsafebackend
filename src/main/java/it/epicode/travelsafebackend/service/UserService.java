package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        user.setEnabled(false);
        userRepository.save(user);
    }
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
