package com.enspy26.gi.external_api.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.enspy26.gi.database_agence_voyage.dto.Payements.PayInDTO;
import com.enspy26.gi.database_agence_voyage.dto.Payements.PayInResult;
import com.enspy26.gi.database_agence_voyage.dto.Payements.StatusResult;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.repositories.ReservationRepository;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;

@Service
public class PayementService {

  UserRepository userRepository;
  ReservationRepository reservationRepository;

  RestTemplate restTemplate;

  @Value("${payment.apiKey}")
  private String apiKey;

  private String BASE_PATH_URL = "https://gateway.yowyob.com/payment-service";

  public PayementService(UserRepository userRepository, ReservationRepository reservationRepository,
      RestTemplate restTemplate) {
    this.userRepository = userRepository;
    this.reservationRepository = reservationRepository;
    this.restTemplate = restTemplate;
  }

  public PayInResult pay(String mobilePhone, String mobilePhoneName, double amount, UUID userId) {

    // On se rassure que l'utilisateur exite
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non existant"));

    PayInDTO payInDTO = new PayInDTO();
    payInDTO.setPayer_phone_number(mobilePhone);
    payInDTO.setTransaction_amount(amount);
    payInDTO.setTransaction_currency("XAF");
    payInDTO.setTransaction_method("MOBILE");

    payInDTO.setPayer_email(user.getEmail());
    payInDTO.setPayer_name(mobilePhoneName);
    payInDTO.setPayer_reference(user.getUserId().toString());

    UUID transactionReference = UUID.randomUUID();
    payInDTO.setTransaction_reference(transactionReference.toString());

    // Autres trucs à définir

    // Définition de la requete
    HttpEntity<?> request = new HttpEntity<>(payInDTO);

    String url = this.BASE_PATH_URL + "/" + this.apiKey + "/payin";

    ResponseEntity<PayInResult> response = restTemplate.exchange(url, HttpMethod.POST, request, PayInResult.class);
    PayInResult payInResult = response.getBody();

    return payInResult;
  }

  public StatusResult payStatus(String transactionCode) {
    String url = this.BASE_PATH_URL + "/" + this.apiKey + "/transactions/" + transactionCode + "/status";

    ResponseEntity<StatusResult> response = restTemplate.exchange(url, HttpMethod.GET, null, StatusResult.class);

    return response.getBody();
  }
}
