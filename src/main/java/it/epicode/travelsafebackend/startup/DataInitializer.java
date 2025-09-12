package it.epicode.travelsafebackend.startup;

import it.epicode.travelsafebackend.entity.Role;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer run called");
        var email = "safetravel130@gmail.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = new User();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("adminPassword123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setNome("Travel");
            admin.setCognome("Safe");
            admin.setNickname("admin");
            userRepository.save(admin);
            System.out.println("Admin creato con ID: " + admin.getId());
        } else {
            System.out.println("Admin già presente");
        }
    }
}
