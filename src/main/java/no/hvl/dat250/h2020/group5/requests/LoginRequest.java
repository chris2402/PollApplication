package no.hvl.dat250.h2020.group5.requests;

import lombok.Data;

@Data
public class LoginRequest {
  String username;
  String password;

  public LoginRequest username(String username) {
    setUsername(username);
    return this;
  }

  public LoginRequest password(String password) {
    setPassword(password);
    return this;
  }
}
