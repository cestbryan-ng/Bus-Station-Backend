package com.enspy26.gi.database_agence_voyage.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeAgenceVoyage {
    @PrimaryKey
    private UUID employeId;

    private UUID agenceVoyageId;
    private UUID userId;
    private String poste; // Titre du poste (ex: "Responsable Commercial", "Assistant", etc.)
    private LocalDateTime dateEmbauche;
    private LocalDateTime dateFinContrat; // null si toujours actif
    private StatutEmploye statutEmploye;
    private Double salaire; // optionnel
    private String departement; // ex: "Commercial", "Administration", "Maintenance"
    private UUID managerId; // ID du supérieur hiérarchique (optionnel)
}