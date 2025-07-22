package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.entity.Notifica;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.repository.NotificaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificaService {

    private final NotificaRepository notificaRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notifica save(Notifica notifica) {
        Notifica saved = notificaRepository.save(notifica);
        // Invio notifiche realtime al topic admin
        messagingTemplate.convertAndSend("/topic/admin-notifiche", saved);
        return saved;
    }

    public List<Notifica> findByUser(User user) {
        return notificaRepository.findByUser(user);
    }

    public void markAsRead(Long id) {
        notificaRepository.findById(id).ifPresent(notifica -> {
            notifica.setLetta(true);
            notificaRepository.save(notifica);
        });
    }
}
