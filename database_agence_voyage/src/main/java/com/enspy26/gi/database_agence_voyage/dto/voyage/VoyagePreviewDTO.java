package com.enspy26.gi.database_agence_voyage.dto.voyage;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.Date;

import com.enspy26.gi.database_agence_voyage.enums.Amenities;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// ce dto est là pour la liste des voyages
public class VoyagePreviewDTO {
    private UUID idVoyage;
    private String nomAgence;
    private String lieuDepart;
    private String lieuArrive;
    private String pointDeDepart;
    private String pointArrivee;
    private int nbrPlaceRestante;
    private int nbrPlaceReservable;
    private Date dateDepartPrev;
    private Duration dureeVoyage;
    private String nomClasseVoyage;
    private double prix;
    private String smallImage;
    private String bigImage;
    private List<Amenities> amenities;
    private StatutVoyage statusVoyage;

}