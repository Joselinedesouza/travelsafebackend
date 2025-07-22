package it.epicode.travelsafebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelsafebackendApplication {

	public static void main(String[] args) {
		System.out.println("inizializzazione applicazione !");
		SpringApplication.run(TravelsafebackendApplication.class, args);
		System.out.println("fine lancio del server!");
	}


}
