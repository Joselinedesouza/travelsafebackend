// NewsDTO.java
package it.epicode.travelsafebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private String title;
    private String description;
    private String url;
    private String sourceName;
    private String publishedAt;
}
