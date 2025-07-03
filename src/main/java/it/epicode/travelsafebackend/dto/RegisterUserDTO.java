package it.epicode.travelsafebackend.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String role;
}