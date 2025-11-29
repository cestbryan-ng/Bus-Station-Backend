package com.enspy26.gi.annulation_reservation.exception;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RegistrationException extends ResponseStatusException {
  private HashMap<String, String> errors;
  private HttpStatus status;

  public RegistrationException(HttpStatus status, HashMap<String, String> errors) {
    super(status);
    this.status = status;
    this.errors = errors;
  }

  public HashMap<String, String> getErrors() {
    return errors;
  }

  public HttpStatus getStatus() {
    return status;
  }
}