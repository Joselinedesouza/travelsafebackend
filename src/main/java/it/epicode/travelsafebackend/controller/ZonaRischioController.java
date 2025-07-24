package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.ZonaPerCittaStatDTO;
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
@RequestMapping({"/api/zone-rischio", "/api/zone-rischi"})
@RequiredArgsConstructor
public class ZonaRischioController {

    private final ZonaRischioService zonaRischioService;

    // Recupera tutte le zone rischio
    @GetMapping
    public List<ZonaRischioResponseDTO> getAll() {
        return zonaRischioService.getAll();
    }

    // Crea una nuova zona rischio (solo admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZonaRischioResponseDTO> create(@RequestBody @Valid ZonaRischioRequestDTO dto) {
        ZonaRischioResponseDTO created = zonaRischioService.crea(dto);
        return ResponseEntity.status(201).body(created);
    }

    // Statistiche per città con livello pericolo prevalente (solo admin)
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ZonaPerCittaStatDTO> getStatisticaZone() {
        return zonaRischioService.getStatisticheZonePerCitta();
    }

    // Recupera zona rischio per ID
    @GetMapping("/{id}")
    public ResponseEntity<ZonaRischioResponseDTO> getById(@PathVariable Long id) {
        try {
            ZonaRischioResponseDTO zona = zonaRischioService.getById(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Aggiorna zona rischio (solo admin)
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
            return ResponseEntity.notFound().build();
        }
    }

    // Elimina zona rischio (solo admin)
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

    // Cerca zone per prossimità geografica
    @GetMapping("/search")
    public List<ZonaRischioResponseDTO> findByProximity(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm) {
        return zonaRischioService.findByProximity(lat, lng, radiusKm);
    }
}
