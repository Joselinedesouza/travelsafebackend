package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.ViaggioRegistratoDTO;
import it.epicode.travelsafebackend.entity.ViaggioRegistrato;
import it.epicode.travelsafebackend.repository.ViaggioRegistratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViaggioRegistratoService {

    private final ViaggioRegistratoRepository viaggioRepository;

    public ViaggioRegistratoDTO saveViaggio(ViaggioRegistratoDTO dto, String userEmail) {
        ViaggioRegistrato viaggio = ViaggioRegistrato.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(userEmail)
                .nomeStrutturaAlloggio(dto.getNomeStrutturaAlloggio())
                .motivoViaggio(dto.getMotivoViaggio())
                .telefonoCompleto(dto.getTelefonoCompleto())
                .numeroEmergenza(dto.getNumeroEmergenza())
                .latitudine(dto.getLatitudine())
                .longitudine(dto.getLongitudine())
                .dataRegistrazione(LocalDateTime.now())
                .build();

        ViaggioRegistrato saved = viaggioRepository.save(viaggio);
        return toDTO(saved);
    }

    public List<ViaggioRegistratoDTO> findTripsByUser(String userEmail) {
        List<ViaggioRegistrato> viaggi = viaggioRepository.findByEmail(userEmail);
        return viaggi.stream()
                .map(this::toDTO)
                .toList();
    }

    private ViaggioRegistratoDTO toDTO(ViaggioRegistrato viaggio) {
        return ViaggioRegistratoDTO.builder()
                .id(viaggio.getId())
                .nome(viaggio.getNome())
                .cognome(viaggio.getCognome())
                .email(viaggio.getEmail())
                .nomeStrutturaAlloggio(viaggio.getNomeStrutturaAlloggio())
                .motivoViaggio(viaggio.getMotivoViaggio())
                .telefonoCompleto(viaggio.getTelefonoCompleto())
                .numeroEmergenza(viaggio.getNumeroEmergenza())
                .latitudine(viaggio.getLatitudine())
                .longitudine(viaggio.getLongitudine())
                .dataRegistrazione(viaggio.getDataRegistrazione())
                .build();
    }
    public void deleteTripByIdAndUser(Long id, String userEmail) {
        ViaggioRegistrato viaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaggio non trovato"));

        if (!viaggio.getEmail().equals(userEmail)) {
            throw new RuntimeException("Non autorizzato a cancellare questo viaggio");
        }
        viaggioRepository.delete(viaggio);
    }

}
