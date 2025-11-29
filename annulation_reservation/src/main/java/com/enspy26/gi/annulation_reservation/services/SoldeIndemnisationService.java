package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.models.SoldeIndemnisation;
import com.enspy26.gi.database_agence_voyage.repositories.SoldeIndemnisationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SoldeIndemnisationService {

    public final SoldeIndemnisationRepository soldeIndemnisationRepository;

    public SoldeIndemnisationService(SoldeIndemnisationRepository soldeIndemnisationRepository) {
        this.soldeIndemnisationRepository = soldeIndemnisationRepository;
    }

    public List<SoldeIndemnisation> findAll() {
        return soldeIndemnisationRepository.findAll();
    }

    public SoldeIndemnisation findById(UUID id) {
        return soldeIndemnisationRepository.findById(id).orElse(null);
    }

    public SoldeIndemnisation create(SoldeIndemnisation soldeIndemnisation) {
        return soldeIndemnisationRepository.save(soldeIndemnisation);
    }

    public SoldeIndemnisation update(SoldeIndemnisation soldeIndemnisation) {
        return soldeIndemnisationRepository.save(soldeIndemnisation);
    }

    public void delete(UUID id) {
        soldeIndemnisationRepository.deleteById(id);
    }
}
