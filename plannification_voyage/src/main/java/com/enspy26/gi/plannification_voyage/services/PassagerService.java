package com.enspy26.gi.plannification_voyage.services;

import com.enspy26.gi.database_agence_voyage.models.Passager;
import com.enspy26.gi.database_agence_voyage.repositories.PassagerRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PassagerService {
    private final PassagerRepository passagerRepository;

    public List<Passager> findAll() {
        return passagerRepository.findAll();
    }

    public Passager findById(UUID id) {
        return passagerRepository.findById(id).orElse(null);
    }

    /*
     * public Passager create(PassagerDTO passagerDTO) {
     * Passager passager = new Passager();
     * passager.setIdPassager(UUID.randomUUID());
     * passager.setNumeroPieceIdentific(passagerDTO.getNumeroPieceIdentific());
     * passager.setNom(passagerDTO.getNom());
     * passager.setGenre(passagerDTO.getGenre());
     * passager.setAge(passagerDTO.getAge());
     * passager.setNbrBaggage(passagerDTO.getNbrBaggage());
     * // passager.setIdReservation(passagerDTO.getIdReservation());
     * return passagerRepository.save(passager);
     * }
     */

    public Passager update(Passager passager, UUID id) {
        return passagerRepository.save(passager);
    }

    public void delete(UUID id) {
        passagerRepository.deleteById(id);
    }

}
