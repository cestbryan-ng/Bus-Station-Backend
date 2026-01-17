package com.enspy26.gi.database_agence_voyage.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenceEvolutionDTO {
    // Pour graphique ligne - Évolution du taux d'occupation
    @JsonProperty("evolution_taux_occupation")
    private List<EvolutionData> evolutionTauxOccupation;

    // Pour graphique ligne - Évolution des annulations
    @JsonProperty("evolution_annulations")
    private List<EvolutionData> evolutionAnnulations;

    // Pour graphique barres comparatif - Revenus par mois (format Map pour faciliter le graphique)
    @JsonProperty("revenue_per_month")
    private Map<String, Double> revenuePerMonth;

    // Pour graphique barres comparatif - Réservations par mois
    @JsonProperty("reservations_per_month")
    private Map<String, Integer> reservationsPerMonth;
    private List<EvolutionData> evolutionReservations;
    private List<EvolutionData> evolutionVoyages;
    private List<EvolutionData> evolutionRevenus;
    private List<EvolutionData> evolutionUtilisateurs;
}