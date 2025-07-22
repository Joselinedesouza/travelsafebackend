package it.epicode.travelsafebackend.repository;

import it.epicode.travelsafebackend.entity.ZonaRischio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZonaRischioRepository extends JpaRepository<ZonaRischio, Long> {

    @Query(value = "SELECT * FROM zona_rischio z WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(z.latitudine)) * cos(radians(z.longitudine) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(z.latitudine)))) < :radiusKm", nativeQuery = true)
    List<ZonaRischio> findByProximity(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm);
}
