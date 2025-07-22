package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.RecensioniRequestDTO;
import it.epicode.travelsafebackend.dto.RecensioniResponseDTO;
import it.epicode.travelsafebackend.dto.ReplyRequestDTO;
import it.epicode.travelsafebackend.service.RecensioniService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews") // coerente con frontend
public class RecensioniController {

    private final RecensioniService recensioniService;

    public RecensioniController(RecensioniService recensioniService){
        this.recensioniService = recensioniService;
    }

    @GetMapping
    public List<RecensioniResponseDTO> getAllReviews() {
        return recensioniService.findAllReviews();
    }

    @GetMapping("/mine")
    public List<RecensioniResponseDTO> getMyReviews(Authentication authentication) {
        String userEmail = authentication.getName();
        return recensioniService.findByUserEmail(userEmail);
    }

    @PostMapping
    public RecensioniResponseDTO createReview(@RequestBody @Valid RecensioniRequestDTO dto, Authentication authentication) {
        String userEmail = authentication.getName();
        return recensioniService.createReview(userEmail, dto);
    }

    @PutMapping("/{id}")
    public RecensioniResponseDTO updateReview(
            @PathVariable Long id,
            @RequestBody @Valid RecensioniRequestDTO dto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return recensioniService.updateReview(id, userEmail, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        recensioniService.deleteReview(id, userEmail);
    }
    @PostMapping("/{id}/reply")
    public RecensioniResponseDTO replyToReview(
            @PathVariable Long id,
            @RequestBody @Valid ReplyRequestDTO replyDTO,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return recensioniService.replyToReview(id, userEmail, replyDTO);
    }

}
