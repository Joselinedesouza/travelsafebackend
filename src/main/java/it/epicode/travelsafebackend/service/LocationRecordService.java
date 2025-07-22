package it.epicode.travelsafebackend.service;

import it.epicode.travelsafebackend.dto.LocationRecordDTO;
import it.epicode.travelsafebackend.entity.LocationRecord;
import it.epicode.travelsafebackend.repository.LocationRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationRecordService {

    private final LocationRecordRepository repo;

    public LocationRecord save(LocationRecordDTO dto){
        LocationRecord entity =  LocationRecord.builder()
                .lat(dto.getLat())
                .lng(dto.getLng())
                .address(dto.getAddress())
                .timestamp(dto.getTimestamp())
                .build();
        return repo.save(entity);
    }
}
