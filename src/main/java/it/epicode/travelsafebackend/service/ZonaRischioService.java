package it.epicode.travelsafebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.epicode.travelsafebackend.dto.ZonaPerCittaStatDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioJsonDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioRequestDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioResponseDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.entity.LivelloPericolo;
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

    private final ZonaRischioRepository zonaRischioRepository;
    private final CittaRepository cittaRepository;
    private final ObjectMapper objectMapper;
    private final boolean usaDatiDemo = false;
    private List<ZonaRischio> datiDemo = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("demo_zone.json");
            List<ZonaRischioJsonDTO> zoneDemo = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<ZonaRischioJsonDTO>>() {}
            );

            for (ZonaRischioJsonDTO dto : zoneDemo) {
                boolean exists = zonaRischioRepository.existsByNomeAndLatitudineAndLongitudine(
                        dto.getNome(), dto.getLatitudine(), dto.getLongitudine()
                );

                if (!exists) {
                    Citta citta = cittaRepository.findByNomeIgnoreCase(dto.getNomeCitta())
                            .orElseGet(() -> cittaRepository.save(
                                    Citta.builder().nome(dto.getNomeCitta()).build()
                            ));

                    ZonaRischio zona = ZonaRischio.builder()
                            .nome(dto.getNome())
                            .descrizione(dto.getDescrizione())
                            .latitudine(dto.getLatitudine())
                            .longitudine(dto.getLongitudine())
                            .livelloPericolo(LivelloPericolo.valueOf(dto.getLivelloPericolo()))
                            .citta(citta)
                            .build();

                    zonaRischioRepository.save(zona);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZonaRischioResponseDTO crea(ZonaRischioRequestDTO dto) {
        Citta citta;

        if (dto.getCittaId() != null) {
            citta = cittaRepository.findById(dto.getCittaId())
                    .orElseThrow(() -> new RuntimeException("Città non trovata con id: " + dto.getCittaId()));
        } else {
            String nomeCitta = (dto.getNomeCitta() != null && !dto.getNomeCitta().isBlank()) ? dto.getNomeCitta() : null;

            if (nomeCitta == null || nomeCitta.isBlank()) {
                throw new RuntimeException("Nome città obbligatorio se non viene fornito l'id");
            }

            citta = new Citta();
            citta.setNome(nomeCitta);
            citta = cittaRepository.save(citta);
        }

        ZonaRischio zona = ZonaRischio.builder()
                .nome(dto.getNome())
                .descrizione(dto.getDescrizione())
                .latitudine(dto.getLatitudine())
                .longitudine(dto.getLongitudine())
                .livelloPericolo(dto.getLivelloPericolo())
                .citta(citta)
                .build();

        zonaRischioRepository.save(zona);

        return toResponseDTO(zona);
    }

    public List<ZonaRischioResponseDTO> getAll() {
        if (usaDatiDemo) {
            return datiDemo.stream().map(this::toResponseDTO).toList();
        }
        return zonaRischioRepository.findAll().stream().map(this::toResponseDTO).toList();
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

        zona.setNome(dto.getNome());
        zona.setDescrizione(dto.getDescrizione());
        zona.setLatitudine(dto.getLatitudine());
        zona.setLongitudine(dto.getLongitudine());
        zona.setLivelloPericolo(dto.getLivelloPericolo());

        if (dto.getNomeCitta() != null && !dto.getNomeCitta().isBlank()) {
            Citta citta = cittaRepository.findByNomeIgnoreCase(dto.getNomeCitta())
                    .orElseGet(() -> cittaRepository.save(Citta.builder().nome(dto.getNomeCitta()).build()));
            zona.setCitta(citta);
        }

        zonaRischioRepository.save(zona);

        return toResponseDTO(zona);
    }

    public void delete(Long id) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata con id: " + id));
        zonaRischioRepository.delete(zona);
    }

    public List<ZonaPerCittaStatDTO> getStatisticheZonePerCitta() {
        return zonaRischioRepository.countZonePerCittaConLivelloPrevalente();
    }

    public List<ZonaRischioResponseDTO> findByProximity(double lat, double lng, double radiusKm) {
        if (usaDatiDemo) {
            return datiDemo.stream()
                    .filter(z -> distanzaKm(lat, lng, z.getLatitudine(), z.getLongitudine()) <= radiusKm)
                    .map(this::toResponseDTO)
                    .toList();
        }
        return zonaRischioRepository.findByProximity(lat, lng, radiusKm).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private double distanzaKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
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
