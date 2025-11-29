package com.enspy26.gi.database_agence_voyage.dto.Payements;

import lombok.Data;

@Data
public class PayInData {
  String message;
  int status_code;
  String transaction_code;
  TransactionStatus transaction_status;
}
