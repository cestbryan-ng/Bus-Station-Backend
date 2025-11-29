package com.enspy26.gi.annulation_reservation.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AnnulationException extends RuntimeException {

  private final HttpStatus httpStatus;
  private final String errorCode;

  /**
   * Constructeur avec message et status HTTP
   */
  public AnnulationException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = generateErrorCode(httpStatus);
  }

  /**
   * Constructeur avec message, status HTTP et cause
   */
  public AnnulationException(String message, HttpStatus httpStatus, Throwable cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.errorCode = generateErrorCode(httpStatus);
  }

  /**
   * Constructeur avec message, status HTTP et code d'erreur personnalisé
   */
  public AnnulationException(String message, HttpStatus httpStatus, String errorCode) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }

  /**
   * Génère un code d'erreur basé sur le status HTTP
   */
  private String generateErrorCode(HttpStatus status) {
    switch (status) {
      case NOT_FOUND:
        return "ANNULATION_RESOURCE_NOT_FOUND";
      case FORBIDDEN:
        return "ANNULATION_ACCESS_DENIED";
      case CONFLICT:
        return "ANNULATION_CONFLICT";
      case BAD_REQUEST:
        return "ANNULATION_INVALID_REQUEST";
      case INTERNAL_SERVER_ERROR:
        return "ANNULATION_INTERNAL_ERROR";
      default:
        return "ANNULATION_UNKNOWN_ERROR";
    }
  }
}