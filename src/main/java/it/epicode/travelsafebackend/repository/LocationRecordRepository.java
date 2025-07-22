package it.epicode.travelsafebackend.repository;

import it.epicode.travelsafebackend.entity.LocationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRecordRepository extends JpaRepository<LocationRecord, Long> {
}
