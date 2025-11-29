package com.enspy26.gi.database_agence_voyage.dto.classVoyage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassVoyageDTO {
    private String nom;
    private double prix;
    private double tauxAnnulation;
    private UUID idAgenceVoyage;
}
