package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.RecensioniRequestDTO;
import it.epicode.travelsafebackend.dto.RecensioniResponseDTO;
import it.epicode.travelsafebackend.dto.ReplyRequestDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.entity.Recensione;
import it.epicode.travelsafebackend.entity.User;
import it.epicode.travelsafebackend.exception.AccessDeniedException;
import it.epicode.travelsafebackend.repository.CittaRepository;
import it.epicode.travelsafebackend.repository.RecensioneRepository;
import it.epicode.travelsafebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecensioniService {

    private final RecensioneRepository recensioneRepository;
    private final UserRepository userRepository;
    private final CittaRepository cittaRepository;

    public List<RecensioniResponseDTO> findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<Recensione> recensioni = recensioneRepository.findByUser(user);

        return recensioni.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public RecensioniResponseDTO createReview(String userEmail, RecensioniRequestDTO dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Citta citta = cittaRepository.findById(dto.getCittaId())
                .orElseThrow(() -> new RuntimeException("Città non trovata"));

        Recensione recensione = Recensione.builder()
                .user(user)
                .citta(citta)
                .testo(dto.getTesto())
                .voto(dto.getVoto())
                .dataCreazione(LocalDateTime.now())
                .build();

        Recensione saved = recensioneRepository.save(recensione);

        return toResponseDTO(saved);
    }

    public RecensioniResponseDTO updateReview(Long id, String userEmail, RecensioniRequestDTO dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Recensione recensione = recensioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));

        if (!recensione.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Non autorizzato a modificare questa recensione");
        }

        Citta citta = cittaRepository.findById(dto.getCittaId())
                .orElseThrow(() -> new RuntimeException("Città non trovata"));

        recensione.setCitta(citta);
        recensione.setTesto(dto.getTesto());
        recensione.setVoto(dto.getVoto());

        Recensione updated = recensioneRepository.save(recensione);

        return toResponseDTO(updated);
    }

    public void deleteReview(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Recensione recensione = recensioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));

        if (!recensione.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Non autorizzato a cancellare questa recensione");
        }

        recensioneRepository.delete(recensione);
    }

    public List<RecensioniResponseDTO> findAllReviews() {
        List<Recensione> recensioni = recensioneRepository.findAll();

        return recensioni.stream()
                .map(this::toResponseDTOWithAuthor)
                .collect(Collectors.toList());
    }

    // Nuovo metodo per rispondere a una recensione
    public RecensioniResponseDTO replyToReview(Long recensioneId, String userEmail, ReplyRequestDTO replyDTO) {
        Recensione recensione = recensioneRepository.findById(recensioneId)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata con id: " + recensioneId));

        // Controllo permessi (esempio: solo admin o autore possono rispondere)
        boolean isAdmin = userRepository.findByEmail(userEmail)
                .map(user -> user.getRole().name().equals("ADMIN"))
                .orElse(false);

        boolean isAutore = recensione.getUser().getEmail().equals(userEmail);

        if (!isAdmin && !isAutore) {
            throw new AccessDeniedException("Non autorizzato a rispondere a questa recensione");
        }

        recensione.setRisposta(replyDTO.getTestoRisposta());
        recensioneRepository.save(recensione);

        return toResponseDTOWithAuthor(recensione);
    }

    private RecensioniResponseDTO toResponseDTO(Recensione recensione) {
        return RecensioniResponseDTO.builder()
                .id(recensione.getId())
                .cittaId(recensione.getCitta().getId())
                .cittaNome(recensione.getCitta().getNome())
                .testo(recensione.getTesto())
                .voto(recensione.getVoto())
                .dataCreazione(recensione.getDataCreazione().toString())
                .build();
    }

    private RecensioniResponseDTO toResponseDTOWithAuthor(Recensione recensione) {
        return RecensioniResponseDTO.builder()
                .id(recensione.getId())
                .cittaId(recensione.getCitta().getId())
                .cittaNome(recensione.getCitta().getNome())
                .testo(recensione.getTesto())
                .voto(recensione.getVoto())
                .dataCreazione(recensione.getDataCreazione().toString())
                .autoreEmail(recensione.getUser().getEmail())
                .risposta(recensione.getRisposta())
                .build();
    }
}
