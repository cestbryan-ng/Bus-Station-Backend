package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour la création/mise à jour d'un employé d'agence
 * Étend UserDTO pour réutiliser les informations utilisateur de base
 */
@Getter
@Setter
public class EmployeRequestDTO extends UserDTO {

    @NotNull(message = "L'ID de l'agence de voyage est requis")
    private UUID agenceVoyageId;

    private String poste;
    private String departement;
    private Double salaire;
    private UUID managerId; // ID du supérieur hiérarchique
    private boolean isUserExist = false; // Si l'utilisateur existe déjà
}