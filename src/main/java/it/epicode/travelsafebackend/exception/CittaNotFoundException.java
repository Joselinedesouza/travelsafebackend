package it.epicode.travelsafebackend.exception;

public class CittaNotFoundException extends RuntimeException {
    public CittaNotFoundException(String message) {
        super(message);
    }
}
