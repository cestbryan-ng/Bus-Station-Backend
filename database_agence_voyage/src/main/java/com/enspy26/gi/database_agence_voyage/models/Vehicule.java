package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "vehicule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicule {
    @Id
    @Column(name = "idvehicule")
    private UUID idVehicule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String modele;

    @Column(nullable = true)
    private String description;

    @Column(name = "nbrplaces", nullable = false)
    private int nbrPlaces;

    @Column(name = "plaquematricule", nullable = true)
    private String plaqueMatricule;

    @Column(name = "lienphoto", nullable = true)
    private String lienPhoto;

    @Column(name = "idagencevoyage", nullable = true)
    private UUID idAgenceVoyage;
}
