package com.enspy26.gi.database_agence_voyage.enums;

public enum StatutReservation {
  RESERVER, // Il a créer la reservation mais pas encore payé
  CONFIRMER, // Le Client à déjà tout payé
  ANNULER, // Le client à annuler la reservation, que ce soit avant ou après payement
  VALIDER
}
