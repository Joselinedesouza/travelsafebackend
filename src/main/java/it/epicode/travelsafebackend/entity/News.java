// News.java
package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;
    private String title;
    @Column(length = 1000)
    private String description;
    private String url;
    private String sourceName;
    private String publishedAt;
}
