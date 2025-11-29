
package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import com.enspy26.gi.database_agence_voyage.models.EmployeAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;


import java.time.LocalDateTime;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeResponseDTO {
    private UUID employeId;
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String poste;
    private String departement;
    private LocalDateTime dateEmbauche;
    private StatutEmploye statutEmploye;
    private String nomManager; // Nom du manager si présent
    private UUID agenceVoyageId;
    private String nomAgence;

    /**
     * Méthode factory pour créer un EmployeResponseDTO à partir des entités
     */
    public static EmployeResponseDTO fromEntities(EmployeAgenceVoyage employe, User user, AgenceVoyage agence, String nomManager) {

        EmployeResponseDTO dto = new EmployeResponseDTO();
        dto.setEmployeId(employe.getEmployeId());
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getPrenom());
        dto.setLastName(user.getNom());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getTelNumber());
        dto.setPoste(employe.getPoste());
        dto.setDepartement(employe.getDepartement());
        dto.setDateEmbauche(employe.getDateEmbauche());
        dto.setStatutEmploye(employe.getStatutEmploye());
        dto.setNomManager(nomManager);
        dto.setAgenceVoyageId(agence.getAgencyId());
        dto.setNomAgence(agence.getLongName());

        return dto;
    }
}