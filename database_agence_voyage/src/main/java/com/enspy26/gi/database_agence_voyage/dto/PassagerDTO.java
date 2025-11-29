package com.enspy26.gi.database_agence_voyage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PassagerDTO {
  String numeroPieceIdentific;
  String nom;
  String genre;
  int age;
  int nbrBaggage;
  int placeChoisis;
}
