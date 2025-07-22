package it.epicode.travelsafebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import it.epicode.travelsafebackend.dto.ZonaRischioRequestDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioResponseDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.entity.ZonaRischio;
import it.epicode.travelsafebackend.repository.CittaRepository;
import it.epicode.travelsafebackend.repository.ZonaRischioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaRischioService {

    private final ZonaRischioRepository zonaRischioRepository;  // Repository JPA per zone rischio
    private final CittaRepository cittaRepository;             // Repository JPA per città
    private final ObjectMapper objectMapper;                   // Mapper Jackson per leggere JSON

    private final boolean usaDatiDemo = true;                  // Flag per usare dati demo JSON o DB

    private List<ZonaRischio> datiDemo = new ArrayList<>();    // Lista dati demo caricata da JSON

    @PostConstruct
    public void init() {
        if (usaDatiDemo) {
            try {
                ClassPathResource resource = new ClassPathResource("demo_zone.json"); // Prendi il file JSON da resources
                datiDemo = objectMapper.readValue(
                        resource.getInputStream(),
                        new TypeReference<List<ZonaRischio>>() {}
                );  // Deserializza JSON in lista di ZonaRischio
            } catch (java.io.IOException e) {
                e.printStackTrace();
                datiDemo = new ArrayList<>();  // In caso di errore, lista vuota
            }
        }
    }

    public ZonaRischioResponseDTO crea(ZonaRischioRequestDTO dto) {
        Citta citta;

        if (dto.getCittaId() != null) {
            // Se l'id città è fornito, cerca la città nel DB o lancia eccezione
            citta = cittaRepository.findById(dto.getCittaId())
                    .orElseThrow(() -> new RuntimeException("Città non trovata con id: " + dto.getCittaId()));
        } else {
            // Se non è fornito l'id, usa il nome città per crearla (necessario)
            String nomeCitta = (dto.getNomeCitta() != null && !dto.getNomeCitta().isBlank())
                    ? dto.getNomeCitta()
                    : null;

            if (nomeCitta == null || nomeCitta.isBlank()) {
                throw new RuntimeException("Nome città obbligatorio se non viene fornito l'id");
            }

            citta = new Citta();
            citta.setNome(nomeCitta);
            citta = cittaRepository.save(citta);  // Salva nuova città
        }

        // Crea nuova ZonaRischio basandosi sui dati ricevuti e associa la città
        ZonaRischio zona = ZonaRischio.builder()
                .nome(dto.getNome())
                .descrizione(dto.getDescrizione())
                .latitudine(dto.getLatitudine())
                .longitudine(dto.getLongitudine())
                .livelloPericolo(dto.getLivelloPericolo())
                .citta(citta)
                .build();

        zonaRischioRepository.save(zona);  // Salva la zona rischio nel DB

        return toResponseDTO(zona);  // Ritorna DTO per risposta API
    }

    public List<ZonaRischioResponseDTO> getAll() {
        if (usaDatiDemo) {
            // Se demo abilitato, ritorna dati demo caricati dal JSON mappandoli in DTO
            return datiDemo.stream()
                    .map(this::toResponseDTO)
                    .toList();
        }
        // Altrimenti leggi dal DB e mappa in DTO
        return zonaRischioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ZonaRischioResponseDTO getById(Long id) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata con id: " + id));

        return toResponseDTO(zona);
    }

    public ZonaRischioResponseDTO update(ZonaRischioRequestDTO dto) {
        Long id = dto.getId();
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata con id: " + id));

        zona.setLivelloPericolo(dto.getLivelloPericolo());  // Aggiorna solo livello di pericolo

        zonaRischioRepository.save(zona);

        return toResponseDTO(zona);
    }

    public void delete(Long id) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata con id: " + id));
        zonaRischioRepository.delete(zona);
    }

    public List<ZonaRischioResponseDTO> findByProximity(double lat, double lng, double radiusKm) {
        if (usaDatiDemo) {
            // Cerca demo filtrando per distanza dal punto dato (lat, lng)
            return datiDemo.stream()
                    .filter(z -> distanzaKm(lat, lng, z.getLatitudine(), z.getLongitudine()) <= radiusKm)
                    .map(this::toResponseDTO)
                    .toList();
        }
        // Cerca nel DB con query custom
        return zonaRischioRepository.findByProximity(lat, lng, radiusKm).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private double distanzaKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raggio Terra in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private ZonaRischioResponseDTO toResponseDTO(ZonaRischio zona) {
        String nomeCitta = (zona.getCitta() != null) ? zona.getCitta().getNome() : "N/D";
        return ZonaRischioResponseDTO.builder()
                .id(zona.getId())
                .nome(zona.getNome())
                .descrizione(zona.getDescrizione())
                .latitudine(zona.getLatitudine())
                .longitudine(zona.getLongitudine())
                .livelloPericolo(zona.getLivelloPericolo())
                .nomeCitta(nomeCitta)
                .build();
    }
}
