package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.entity.Citta;
import it.epicode.travelsafebackend.repository.CittaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citta")
@RequiredArgsConstructor
public class CittaController {

    private final CittaRepository cittaRepository;

    @GetMapping
    public List<Citta> getAll() {
        return cittaRepository.findAll();
    }

    @PostMapping
    public Citta create(@RequestBody Citta citta) {
        return cittaRepository.save(citta);
    }
}
