package it.epicode.travelsafebackend.controller;


import it.epicode.travelsafebackend.dto.LocationRecordDTO;
import it.epicode.travelsafebackend.entity.LocationRecord;
import it.epicode.travelsafebackend.service.LocationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationRecordController {

    private final LocationRecordService locationRecordService;

    @PostMapping("/save")
    public ResponseEntity<LocationRecord> saveLocation(@RequestBody LocationRecordDTO dto){
        LocationRecord saved = locationRecordService.save(dto);
        return  ResponseEntity.ok(saved);
    }
}
