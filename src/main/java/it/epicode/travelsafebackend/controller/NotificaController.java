package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.NotificaDTO;
import it.epicode.travelsafebackend.entity.Notifica;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.service.NotificaService;
import it.epicode.travelsafebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {

    private final NotificaService notificaService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<NotificaDTO>> getUserNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<Notifica> notifiche = notificaService.findByUser(user);
        List<NotificaDTO> dtos = notifiche.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private NotificaDTO toDTO(Notifica notifica) {
        return new NotificaDTO(
                notifica.getId(),
                notifica.getMessaggio(),
                notifica.isLetta(),
                notifica.getTimestamp(),
                notifica.getUser() != null ? notifica.getUser().getEmail() : null
        );
    }

    /**
     * Metodo per inviare notifiche real-time agli admin
     */
    public void sendRealtimeNotificationToAdmins(Notifica notifica) {
        NotificaDTO dto = toDTO(notifica);
        messagingTemplate.convertAndSend("/topic/notifiche-admin", dto);
    }
}
