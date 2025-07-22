package it.epicode.travelsafebackend.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificaDTO {
    private Long id;
    private String messaggio;
    private boolean letta;
    private LocalDateTime timestamp;
    // eventualmente info essenziali dell'utente, ad esempio username o email
    private String userEmail;
}
