package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;

import com.enspy26.gi.database_agence_voyage.enums.Amenities;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.enspy26.gi.database_agence_voyage.utils.DurationConverter;

@Entity
@Table(name = "voyage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voyage {
    @Id
    @Column(name = "idvoyage")
    private UUID idVoyage;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = true)
    private String description;

    @Column(name = "datedepartprev", nullable = true)
    private Date dateDepartPrev;

    @Column(name = "lieudepart", nullable = true)
    private String lieuDepart;

    @Column(name = "datedeparteffectif", nullable = true)
    private Date dateDepartEffectif;

    @Column(name = "datearriveeffectif", nullable = true)
    private Date dateArriveEffectif;

    @Column(name = "lieuarrive", nullable = true)
    private String lieuArrive;

    @Column(name = "zonearrive", nullable = true)
    private String zoneArrive;

    @Column(name = "zonedepart", nullable = true)
    private String zoneDepart;

    @Column(name = "heuredeparteffectif", nullable = true)
    private Date heureDepartEffectif;

    @Column(name = "pointdedepart", nullable = true)
    private String pointDeDepart;

    @Column(name = "pointarrivee", nullable = true)
    private String pointArrivee;

    @Convert(converter = DurationConverter.class)
    @Column(name = "dureevoyage", nullable = true)
    private Duration dureeVoyage;

    @Column(name = "heurearrive", nullable = true)
    private Date heureArrive;

    @Column(name = "nbrplacereservable", nullable = false)
    private int nbrPlaceReservable;

    @Column(name = "nbrplacereserve", nullable = false)
    private int nbrPlaceReserve;

    @Column(name = "nbrplaceconfirm", nullable = false)
    private int nbrPlaceConfirm;

    @Column(name = "nbrplacerestante", nullable = false)
    private int nbrPlaceRestante;

    @Column(name = "datepublication", nullable = true)
    private Date datePublication;

    @Column(name = "datelimitereservation", nullable = true)
    private Date dateLimiteReservation;

    @Column(name = "datelimiteconfirmation", nullable = true)
    private Date dateLimiteConfirmation;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusvoyage", nullable = true)
    private StatutVoyage statusVoyage;

    @Column(name = "smallimage", nullable = true)
    private String smallImage;

    @Column(name = "bigimage", nullable = true)
    private String bigImage;

    @Column(name = "amenities", nullable = true, columnDefinition = "text")
    private String amenities; // stored as comma-separated string

    @Transient
    public List<Amenities> getAmenitiesList() {
        if (amenities == null || amenities.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(amenities.split(","))
                .map(Amenities::valueOf)
                .collect(Collectors.toList());
    }

    public void setAmenitiesList(List<Amenities> amenitiesList) {
        if (amenitiesList == null || amenitiesList.isEmpty()) {
            this.amenities = "";
        } else {
            this.amenities = amenitiesList.stream()
                    .map(Amenities::name)
                    .collect(Collectors.joining(","));
        }
    }
}
