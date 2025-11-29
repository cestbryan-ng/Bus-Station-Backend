package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.models.PolitiqueAnnulation;
import com.enspy26.gi.database_agence_voyage.repositories.PolitiqueAnnulationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PolitiqueAnnulationService {

    private final PolitiqueAnnulationRepository politiqueAnnulationRepository;

    public PolitiqueAnnulationService(PolitiqueAnnulationRepository politiqueAnnulationRepository) {
        this.politiqueAnnulationRepository = politiqueAnnulationRepository;
    }

    public List<PolitiqueAnnulation> findAll() {
        return politiqueAnnulationRepository.findAll();
    }

    public PolitiqueAnnulation findById(UUID id) {
        return politiqueAnnulationRepository.findById(id).orElse(null);
    }

    public PolitiqueAnnulation create(PolitiqueAnnulation politiqueAnnulation) {
        return politiqueAnnulationRepository.save(politiqueAnnulation);
    }

    public PolitiqueAnnulation update(PolitiqueAnnulation politiqueAnnulation) {
        return politiqueAnnulationRepository.save(politiqueAnnulation);
    }

    public void delete(UUID id) {
        politiqueAnnulationRepository.deleteById(id);
    }
}
