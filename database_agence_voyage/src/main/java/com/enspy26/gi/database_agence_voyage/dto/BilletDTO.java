package com.enspy26.gi.database_agence_voyage.dto;

import java.time.Duration;
import java.util.Date;

import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BilletDTO {
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
  private StatutVoyage statusVoyage;
  private String smallImage;
  private String bigImage;
  private String nomClasseVoyage;
  private double prix;
  private String nomAgence;
  private String pointDeDepart;
  private String pointArrivee;
  String numeroPieceIdentific;
  String nom;
  String genre;
  int age;
  int nbrBaggage;
  int placeChoisis;
}
