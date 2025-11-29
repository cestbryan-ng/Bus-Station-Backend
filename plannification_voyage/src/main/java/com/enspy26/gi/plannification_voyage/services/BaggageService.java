package com.enspy26.gi.plannification_voyage.services;

import com.enspy26.gi.database_agence_voyage.models.Baggage;
import com.enspy26.gi.database_agence_voyage.repositories.BaggageRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaggageService {

    private final BaggageRepository baggageRepository;

    public List<Baggage> findAll() {
        return baggageRepository.findAll();
    }

    public Baggage findById(UUID id) {
        return baggageRepository.findById(id).orElse(null);
    }

    public Baggage create(Baggage baggage) {
        return baggageRepository.save(baggage);
    }

    public Baggage update(Baggage baggage) {
        return baggageRepository.save(baggage);
    }

    public void delete(UUID id) {
        baggageRepository.deleteById(id);
    }
}
