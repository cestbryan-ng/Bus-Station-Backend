package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;

@Entity
@Table(name = "employeagencevoyage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeAgenceVoyage {

    @Id
    @Column(name = "employeid")
    private UUID employeId;

    @Column(name = "agencevoyageid", nullable = false)
    private UUID agenceVoyageId;

    @Column(name = "userid", nullable = false)
    private UUID userId;

    @Column(name = "poste")
    private String poste;

    @Column(name = "dateembauche")
    private LocalDateTime dateEmbauche;

    @Column(name = "datefincontrat")
    private LocalDateTime dateFinContrat;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutemploye")
    private StatutEmploye statutEmploye;

    @Column(name = "salaire")
    private Double salaire;

    @Column(name = "departement")
    private String departement;

    @Column(name = "managerid")
    private UUID managerId;
}

