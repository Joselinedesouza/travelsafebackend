package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.ViaggioRegistratoDTO;
import it.epicode.travelsafebackend.service.ViaggioRegistratoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viaggi")
@RequiredArgsConstructor
public class ViaggioRegistratoController {

    private final ViaggioRegistratoService viaggioService;
    @GetMapping("/mine")
    public ResponseEntity<List<ViaggioRegistratoDTO>> getMyTrips(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ViaggioRegistratoDTO> myTrips = viaggioService.findTripsByUser(userEmail);
        return ResponseEntity.ok(myTrips);
    }
    @PostMapping("/mine")
    public ResponseEntity<ViaggioRegistratoDTO> saveViaggio(@RequestBody ViaggioRegistratoDTO dto, Authentication authentication) {
        String userEmail = authentication.getName();
        ViaggioRegistratoDTO saved = viaggioService.saveViaggio(dto, userEmail);
        return ResponseEntity.ok(saved);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViaggio(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        viaggioService.deleteTripByIdAndUser(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
