package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.CittaRequestDTO;
import it.epicode.travelsafebackend.dto.CittaResponseDTO;
import it.epicode.travelsafebackend.service.CittaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citta")
@RequiredArgsConstructor
public class CittaController {

    private final CittaService cittaService;

    @GetMapping
    public ResponseEntity<List<CittaResponseDTO>> getAll() {
        List<CittaResponseDTO> list = cittaService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CittaResponseDTO> getById(@PathVariable Long id) {
        CittaResponseDTO citta = cittaService.getById(id);
        if (citta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(citta);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CittaResponseDTO> createCitta(@RequestBody @Valid CittaRequestDTO cittaDTO) {
        CittaResponseDTO created = cittaService.create(cittaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CittaResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CittaRequestDTO dto) {
        CittaResponseDTO updated = cittaService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = cittaService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
