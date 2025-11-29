package com.enspy26.gi.database_agence_voyage.dto.voyage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.dto.Utilisateur.UserResponseDTO;
import com.enspy26.gi.database_agence_voyage.enums.Amenities;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoyageDetailsDTO {
    private UUID idVoyage;
    private String titre;
    private String description;
    private Date dateDepartPrev;
    private String lieuDepart;
    private Date dateDepartEffectif;
    private Date dateArriveEffectif;
    private String lieuArrive;
    private Date heureDepartEffectif;
    private Duration dureeVoyage;
    private Date heureArrive;
    private int nbrPlaceReservable;
    private int nbrPlaceRestante;
    private Date datePublication;
    private Date dateLimiteReservation;
    private Date dateLimiteConfirmation;
    private StatutVoyage statusVoyage;
    private String smallImage;
    private String bigImage;
    private String nomClasseVoyage;
    private double prix;
    private String nomAgence;
    private String pointDeDepart;
    private String pointArrivee;
    private Vehicule vehicule;
    private UserResponseDTO chauffeur;
    private List<Integer> placeReservees;
    private List<Amenities> amenities;
}
