package com.enspy26.gi.database_agence_voyage.models;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.enspy26.gi.database_agence_voyage.enums.Amenities;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voyage {
  @PrimaryKey
  private UUID idVoyage;
  private String titre;
  private String description;
  private Date dateDepartPrev;
  private String lieuDepart;
  private Date dateDepartEffectif;
  private Date dateArriveEffectif;
  private String lieuArrive;
  private Date heureDepartEffectif;
  private String pointDeDepart;
  private String pointArrivee;
  private Duration dureeVoyage;
  private Date heureArrive;
  private int nbrPlaceReservable;// Nbre de place qu'on peut encore reserve
  private int nbrPlaceReserve;// Nbre de place qu'on a reserve
  private int nbrPlaceConfirm;// Nbre de place qu'on a confirmer
  private int nbrPlaceRestante;//
  private Date datePublication;
  private Date dateLimiteReservation;
  private Date dateLimiteConfirmation;
  private StatutVoyage statusVoyage;
  private String smallImage;
  private String bigImage;

  private String amenities; // JSON string for amenities

  public void setAmenities(List<Amenities> amenitiesList) {
    if (amenitiesList == null || amenitiesList.isEmpty()) {
      this.amenities = "";
      return;
    }
    this.amenities = amenitiesList.stream()
        .map(Amenities::name)
        .reduce((a, b) -> a + "," + b)
        .orElse("");
  }

  public List<Amenities> getAmenities() {
    if (amenities == null || amenities.isEmpty()) {
      return List.of();
    }
    return List.of(amenities.split(",")).stream()
        .map(Amenities::valueOf)
        .toList();
  }
}
