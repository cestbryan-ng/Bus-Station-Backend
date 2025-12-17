package com.enspy26.gi.database_agence_voyage.dto.voyage;

import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.enspy26.gi.database_agence_voyage.enums.Amenities;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoyageDTO {
    private String titre;
    private String description;
    private Date dateDepartPrev;
    private String lieuDepart;
    private String lieuArrive;
    private String zoneArrive;
    private String zoneDepart;
    private Date heureDepartEffectif;
    private Duration dureeVoyage;
    private Date heureArrive;
    private Date datePublication;
    private Date dateLimiteReservation;
    private Date dateLimiteConfirmation;
    private StatutVoyage statusVoyage;
    private String smallImage;
    private String bigImage;
    private List<Amenities> amenities;

}
