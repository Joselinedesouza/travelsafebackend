package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.CittaRequestDTO;
import it.epicode.travelsafebackend.dto.CittaResponseDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.exception.CittaNotFoundException;
import it.epicode.travelsafebackend.repository.CittaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CittaService {

    private final CittaRepository cittaRepository;

    public List<CittaResponseDTO> getAll() {
        return cittaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CittaResponseDTO getById(Long id) {
        Citta citta = cittaRepository.findById(id)
                .orElseThrow(() -> new CittaNotFoundException("Città non trovata con id: " + id));
        return toDTO(citta);
    }

    public CittaResponseDTO create(CittaRequestDTO dto) {
        Citta citta = new Citta();
        citta.setNome(dto.getNome());
        citta.setDescrizione(dto.getDescrizione());
        citta.setLatitudine(dto.getLatitudine());
        citta.setLongitudine(dto.getLongitudine());

        Citta saved = cittaRepository.save(citta);
        return toDTO(saved);
    }

    public CittaResponseDTO update(Long id, CittaRequestDTO dto) {
        Citta citta = cittaRepository.findById(id)
                .orElseThrow(() -> new CittaNotFoundException("Città non trovata con id: " + id));

        citta.setNome(dto.getNome());
        citta.setDescrizione(dto.getDescrizione());
        citta.setLatitudine(dto.getLatitudine());
        citta.setLongitudine(dto.getLongitudine());

        Citta saved = cittaRepository.save(citta);
        return toDTO(saved);
    }

    public boolean delete(Long id) {
        Citta citta = cittaRepository.findById(id)
                .orElseThrow(() -> new CittaNotFoundException("Città non trovata con id: " + id));
        cittaRepository.delete(citta);
        return true;
    }

    private CittaResponseDTO toDTO(Citta citta) {
        return CittaResponseDTO.builder()
                .id(citta.getId())
                .nome(citta.getNome())
                .descrizione(citta.getDescrizione())
                .latitudine(citta.getLatitudine())
                .longitudine(citta.getLongitudine())
                .build();
    }
}
