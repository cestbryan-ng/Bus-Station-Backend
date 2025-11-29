package com.enspy26.gi.database_agence_voyage.dto.Payements;

import lombok.Data;

@Data
public class StatusResult {
  ResultStatus status;
  String message;
  StatusData data;
  String errors;
  boolean ok;
}
