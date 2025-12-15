package com.enspy26.gi.annulation_reservation.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgenceVoyageService {

    private final AgenceVoyageRepository agenceVoyageRepository;

    public AgenceVoyage findAgenceByChefAgenceId(UUID chefAgenceByIdid) {
        List<AgenceVoyage> agences = this.agenceVoyageRepository.findByUserId(chefAgenceByIdid);
        if (agences.isEmpty()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
                    "Agence not found for chefAgenceById: " + chefAgenceByIdid);
        }
        return agences.get(0);
    }


    public AgenceVoyage findAgenceById(UUID id) {
        return this.agenceVoyageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Agence not found with id: " + id));
    }
}
