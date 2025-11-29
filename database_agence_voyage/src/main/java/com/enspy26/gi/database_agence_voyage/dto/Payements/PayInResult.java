package com.enspy26.gi.database_agence_voyage.dto.Payements;

import lombok.Data;

@Data
public class PayInResult {
  ResultStatus status;
  String message;
  PayInData data;
  PayInErrors errors;
  boolean ok;
}
