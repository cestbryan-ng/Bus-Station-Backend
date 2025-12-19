package com.enspy26.gi.agence_de_voyage_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableJpaRepositories(basePackages = {
        "com.enspy26.gi.database_agence_voyage.repositories"
})
@EntityScan(basePackages = {
        "com.enspy26.gi.database_agence_voyage.models"
})
@ComponentScan(basePackages = {
        "com.enspy26.gi.agence_de_voyage_application",
        "com.enspy26.gi.database_agence_voyage",
        "com.enspy26.gi.annulation_reservation",
        "com.enspy26.gi.external_api",
        "com.enspy26.gi.plannification_voyage",
        "com.enspy26.gi.notification"
})
public class AgenceDeVoyageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenceDeVoyageApplication.class, args);
    }

}
