package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.enspy26.gi.database_agence_voyage.enums.StatutChauffeur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chauffeuragencevoyage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChauffeurAgenceVoyage {

    @Id
    @Column(name = "chauffeurid")
    private UUID chauffeurId;

    @Column(name = "agencevoyageid")
    private UUID agenceVoyageId;

    @Column(name = "userid")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statuschauffeur")
    private StatutChauffeur statusChauffeur;
}
