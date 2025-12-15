package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ligne_voyage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LigneVoyage {

    @Id
    @Column(name = "idlignevoyage")
    private UUID idLigneVoyage;

    @Column(name = "idclassvoyage", nullable = false)
    private UUID idClassVoyage;

    @Column(name = "idvehicule", nullable = false)
    private UUID idVehicule;

    @Column(name = "idvoyage", nullable = false)
    private UUID idVoyage;

    @Column(name = "idagencevoyage", nullable = false)
    private UUID idAgenceVoyage;

    @Column(name = "idchauffeur", nullable = false)
    private UUID idChauffeur;
}

