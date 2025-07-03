package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.ZonaRischioRequestDTO;
import it.epicode.travelsafebackend.dto.ZonaRischioResponseDTO;
import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.entity.ZonaRischio;
import it.epicode.travelsafebackend.repository.CittaRepository;
import it.epicode.travelsafebackend.repository.ZonaRischioRepository;
import it.epicode.travelsafebackend.service.ZonaRischioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone-rischio")
@RequiredArgsConstructor
public class ZonaRischioController {

    private final ZonaRischioRepository zonaRischioRepository;
    private final ZonaRischioService zonaRischioService;


    @GetMapping
    public List<ZonaRischio> getAll() {
        return zonaRischioRepository.findAll();
    }


    @PostMapping
    public ZonaRischioResponseDTO create(@RequestBody @Valid ZonaRischioRequestDTO dto) {
        return zonaRischioService.crea(dto);
    }
    @GetMapping("/{id}")
    public ZonaRischioResponseDTO getById(@PathVariable Long id) {
        return zonaRischioService.getById(id);
    }

    @PutMapping("/{id}")
    public ZonaRischioResponseDTO update(@PathVariable Long id, @RequestBody @Valid ZonaRischioRequestDTO dto) {
        return zonaRischioService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        zonaRischioService.delete(id);
    }



}

