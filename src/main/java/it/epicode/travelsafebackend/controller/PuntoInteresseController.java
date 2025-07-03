package it.epicode.travelsafebackend.controller;


import it.epicode.travelsafebackend.entity.PuntoInteresse;
import it.epicode.travelsafebackend.repository.PuntoInteresseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/poi")
@RequiredArgsConstructor
public class PuntoInteresseController {

    private final PuntoInteresseRepository puntoInteresseRepository;

    @GetMapping
    public List<PuntoInteresse> getAllPoi() {
        return puntoInteresseRepository.findAll();
    }

    @PostMapping
    public PuntoInteresse createPoi(@RequestBody PuntoInteresse poi) {
        return puntoInteresseRepository.save(poi);
    }

    @GetMapping("/{id}")
    public PuntoInteresse getPoiById(@PathVariable Long id) {
        return puntoInteresseRepository.findById(id).orElseThrow(() -> new RuntimeException("POI non trovato"));
    }
}
