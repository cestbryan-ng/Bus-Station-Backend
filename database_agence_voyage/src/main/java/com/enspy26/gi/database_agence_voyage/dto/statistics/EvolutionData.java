package com.enspy26.gi.database_agence_voyage.dto.statistics;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvolutionData {
    private LocalDate date;
    private long valeur;
    private double montant; // Pour les revenus
}