package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.ZonaRischioRequestDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioResponseDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.entity.ZonaRischio;
import it.epicode.travelsafebackend.repository.CittaRepository;
import it.epicode.travelsafebackend.repository.ZonaRischioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaRischioService {

    private final ZonaRischioRepository zonaRischioRepository;
    private final CittaRepository cittaRepository;

    public ZonaRischioResponseDTO crea(ZonaRischioRequestDTO dto) {
        Citta citta = cittaRepository.findById(dto.getCittaId())
                .orElseThrow(() -> new RuntimeException("Città non trovata"));

        ZonaRischio zona = ZonaRischio.builder()
                .nome(dto.getNome())
                .descrizione(dto.getDescrizione())
                .latitudine(dto.getLatitudine())
                .longitudine(dto.getLongitudine())
                .livelloPericolo(dto.getLivelloPericolo())
                .citta(citta)
                .build();

        zonaRischioRepository.save(zona);

        return ZonaRischioResponseDTO.builder()
                .id(zona.getId())
                .nome(zona.getNome())
                .descrizione(zona.getDescrizione())
                .latitudine(zona.getLatitudine())
                .longitudine(zona.getLongitudine())
                .livelloPericolo(zona.getLivelloPericolo())
                .nomeCitta(citta.getNome())
                .build();
    }

    public List<ZonaRischio> getAll() {
        return zonaRischioRepository.findAll();
    }
    public ZonaRischioResponseDTO getById(Long id) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata"));

        return ZonaRischioResponseDTO.builder()
                .id(zona.getId())
                .nome(zona.getNome())
                .descrizione(zona.getDescrizione())
                .latitudine(zona.getLatitudine())
                .longitudine(zona.getLongitudine())
                .livelloPericolo(zona.getLivelloPericolo())
                .nomeCitta(zona.getCitta().getNome())
                .build();
    }

    public ZonaRischioResponseDTO update(Long id, ZonaRischioRequestDTO dto) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata"));

        Citta citta = cittaRepository.findById(dto.getCittaId())
                .orElseThrow(() -> new RuntimeException("Città non trovata"));

        zona.setNome(dto.getNome());
        zona.setDescrizione(dto.getDescrizione());
        zona.setLatitudine(dto.getLatitudine());
        zona.setLongitudine(dto.getLongitudine());
        zona.setLivelloPericolo(dto.getLivelloPericolo());
        zona.setCitta(citta);

        zonaRischioRepository.save(zona);

        return getById(zona.getId());
    }

    public void delete(Long id) {
        ZonaRischio zona = zonaRischioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona non trovata"));
        zonaRischioRepository.delete(zona);
    }
}
