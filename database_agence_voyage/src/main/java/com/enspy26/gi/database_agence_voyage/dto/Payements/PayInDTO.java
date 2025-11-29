package com.enspy26.gi.database_agence_voyage.dto.Payements;

import lombok.Data;

@Data
public class PayInDTO {
  double transaction_amount;
  String transaction_currency = "XAF";
  String transaction_method = "MOBILE";
  String transaction_reference;
  String payer_reference;
  String payer_name;
  String payer_phone_number;
  String payer_lang;
  String payer_email;
  String service_reference;
  String service_name;
  String service_description;
  int service_quantity;

  public PayInDTO() {
  }
}
