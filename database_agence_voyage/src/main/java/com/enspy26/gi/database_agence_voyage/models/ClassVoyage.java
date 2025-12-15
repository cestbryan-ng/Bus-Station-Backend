package com.enspy26.gi.database_agence_voyage.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.UUID;

@Entity
@Table(name = "classvoyage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassVoyage {

    @Id
    @Column(name = "idclassvoyage")
    private UUID idClassVoyage;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prix")
    private double prix;

    @Column(name = "tauxannulation")
    private double tauxAnnulation;

    @Column(name = "idagencevoyage")
    private UUID idAgenceVoyage;
}
