package it.epicode.travelsafebackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        private Source source;
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Source {
            private String id;
            private String name;
        }
    }
}