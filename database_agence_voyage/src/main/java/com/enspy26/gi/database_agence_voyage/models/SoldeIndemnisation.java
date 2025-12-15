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
@Table(name = "soldeindemnisation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoldeIndemnisation {

    @Id
    private UUID idSolde;

    @Column(nullable = false)
    private double solde;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private UUID idUser;

    @Column(nullable = false)
    private UUID idAgenceVoyage;
}
