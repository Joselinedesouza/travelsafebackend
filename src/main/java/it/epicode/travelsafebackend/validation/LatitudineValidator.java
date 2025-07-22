package it.epicode.travelsafebackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Classe che implementa la logica di validazione personalizzata per il campo latitudine.
 * Verifica che il valore Double sia compreso tra -90 e 90.
 */
public class LatitudineValidator implements ConstraintValidator<ValidLatitudine, Double> {

    /**
     * Metodo che esegue la validazione vera e propria.
     * @param value valore da validare (latitudine)
     * @param context contesto della validazione (non usato qui)
     * @return true se valore valido, false altrimenti
     */
    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        // Verifica che il valore non sia null e sia compreso tra -90 e 90
        return value != null && value >= -90 && value <= 90;
    }
}
