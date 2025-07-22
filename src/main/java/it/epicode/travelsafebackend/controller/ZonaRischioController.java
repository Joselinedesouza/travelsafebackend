package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.ZonaRischioRequestDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioResponseDTO;
import it.epicode.travelsafebackend.service.ZonaRischioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone-rischio")
@RequiredArgsConstructor
public class ZonaRischioController {

    private final ZonaRischioService zonaRischioService;

    // Recupera tutte le zone rischio, accesso libero
    @GetMapping
    public List<ZonaRischioResponseDTO> getAll() {
        return zonaRischioService.getAll();
    }

    // Crea una nuova zona rischio, solo ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZonaRischioResponseDTO> create(@RequestBody @Valid ZonaRischioRequestDTO dto) {
        ZonaRischioResponseDTO created = zonaRischioService.crea(dto);
        return ResponseEntity.status(201).body(created);
    }

    // Recupera una zona rischio per id, accesso libero
    @GetMapping("/{id}")
    public ResponseEntity<ZonaRischioResponseDTO> getById(@PathVariable Long id) {
        try {
            ZonaRischioResponseDTO zona = zonaRischioService.getById(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            // Se non trovato ritorna 404
            return ResponseEntity.notFound().build();
        }
    }

    // Aggiorna una zona rischio per id, solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZonaRischioResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid ZonaRischioRequestDTO dto) {
        dto.setId(id);
        try {
            ZonaRischioResponseDTO updated = zonaRischioService.update(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Se la zona non esiste, ritorna 404
            return ResponseEntity.notFound().build();
        }
    }

    // Elimina una zona rischio per id, solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            zonaRischioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cerca zone rischio entro un raggio geografico, accesso libero
    @GetMapping("/search")
    public List<ZonaRischioResponseDTO> findByProximity(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm) {
        return zonaRischioService.findByProximity(lat, lng, radiusKm);
    }
}