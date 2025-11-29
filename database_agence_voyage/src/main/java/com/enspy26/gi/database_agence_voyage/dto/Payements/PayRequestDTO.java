package com.enspy26.gi.database_agence_voyage.dto.Payements;

import java.util.UUID;

import lombok.Data;

@Data
public class PayRequestDTO {
  String mobilePhone;
  String mobilePhoneName;
  double amount;
  UUID userId;
  UUID reservationId;
}
