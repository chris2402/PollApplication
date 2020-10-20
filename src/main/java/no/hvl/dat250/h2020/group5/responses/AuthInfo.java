package no.hvl.dat250.h2020.group5.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthInfo {
  String jwt;
  List<String> roles;

  public AuthInfo jwt(String jwt) {
    this.jwt = jwt;
    return this;
  }

  public AuthInfo roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
