package it.epicode.travelsafebackend.controller;

import it.epicode.travelsafebackend.dto.NewsDTO;
import it.epicode.travelsafebackend.service.NewsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getNewsByCity(@RequestParam String city) {
        try {
            List<NewsDTO> news = newsService.getNewsByCity(city);
            if (news.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}

