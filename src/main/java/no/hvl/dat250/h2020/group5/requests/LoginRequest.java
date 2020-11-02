package no.hvl.dat250.h2020.group5.requests;

import lombok.Data;

@Data
public class LoginRequest {
  String email;
  String password;

  public LoginRequest email(String email) {
    setEmail(email);
    return this;
  }

  public LoginRequest password(String password) {
    setPassword(password);
    return this;
  }
}
