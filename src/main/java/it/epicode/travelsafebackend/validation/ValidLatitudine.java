package it.epicode.travelsafebackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione personalizzata per validare un campo Double come latitudine geografica valida.
 * Può essere applicata a campi (FIELD).
 */
@Target({ ElementType.FIELD }) // Indica che l’annotazione può essere applicata solo su campi
@Retention(RetentionPolicy.RUNTIME) // L’annotazione è disponibile a runtime per il framework di validazione
@Constraint(validatedBy = LatitudineValidator.class) // Specifica la classe che esegue la validazione
public @interface ValidLatitudine {

    // Messaggio di errore di default in caso di validazione fallita
    String message() default "Latitudine non valida";

    // Gruppi per validazioni condizionali (lascia vuoto se non usati)
    Class<?>[] groups() default {};

    // Payload per fornire informazioni addizionali (lascia vuoto se non usato)
    Class<? extends Payload>[] payload() default {};
}
