package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String token;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false)
        private LocalDateTime expiryDate;

        public PasswordResetToken(String token, User user, LocalDateTime expiryDate) {
                this.token = token;
                this.user = user;
                this.expiryDate = expiryDate;
        }
}
