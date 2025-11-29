package com.enspy26.gi.database_agence_voyage.dto.statistics;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenceEvolutionDTO {
    private List<EvolutionData> evolutionReservations;
    private List<EvolutionData> evolutionVoyages;
    private List<EvolutionData> evolutionRevenus;
    private List<EvolutionData> evolutionUtilisateurs;
}