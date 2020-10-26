package no.hvl.dat250.h2020.group5.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException {
  public UsernameAlreadyTakenException(String message) {
    super(message);
  }
}
