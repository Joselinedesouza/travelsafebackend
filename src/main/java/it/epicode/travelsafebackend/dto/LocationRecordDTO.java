package it.epicode.travelsafebackend.dto;


import lombok.Data;

@Data
public class LocationRecordDTO {
    private double lat;
    private double lng;
    private String address;
    private long timestamp;
}