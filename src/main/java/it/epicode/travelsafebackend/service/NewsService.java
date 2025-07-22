package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.NewsDTO;
import it.epicode.travelsafebackend.entity.NewsApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${gnews.apiKey}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://gnews.io/api/v4")
            .build();

    public List<NewsDTO> fetchNewsByCity(String cityName) {
        // Parole chiave per filtrare notizie su crimini
        String keywords = "furto OR rapina OR incidente OR violenza OR polizia OR crimine OR borseggio";
        String query = cityName + " " + keywords;

        try {
            NewsApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("lang", "it")
                            .queryParam("token", apiKey)
                            .queryParam("max", 10)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResponse -> Mono.error(new WebClientResponseException(
                                    "Errore chiamata GNews API: " + clientResponse.statusCode().value(),
                                    clientResponse.statusCode().value(),
                                    "Errore HTTP",
                                    null, null, null)))
                    .bodyToMono(NewsApiResponse.class)
                    .block(Duration.ofSeconds(10));

            if (response == null || response.getArticles() == null) {
                return List.of();
            }

            return response.getArticles().stream()
                    .map(article -> NewsDTO.builder()
                            .title(Objects.requireNonNullElse(article.getTitle(), "Titolo non disponibile"))
                            .description(Objects.requireNonNullElse(article.getDescription(), "Descrizione non disponibile"))
                            .url(article.getUrl())
                            .sourceName(article.getSource() != null ? article.getSource().getName() : "Sconosciuto")
                            .publishedAt(article.getPublishedAt())
                            .build())
                    .toList();

        } catch (Exception e) {
            System.err.println("Errore nel fetchNewsByCity: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<NewsDTO> getNewsByCity(String city) {
        return fetchNewsByCity(city);
    }
}
