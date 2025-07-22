package it.epicode.travelsafebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double lat;
    private double lng;

    @Column(length = 1050)
    private String address;

    private long timestamp;
}
