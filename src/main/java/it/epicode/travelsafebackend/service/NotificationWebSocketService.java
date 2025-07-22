package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.Notifica;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notifica notifica) {
        messagingTemplate.convertAndSend("/topic/notifiche", notifica);
    }
}
