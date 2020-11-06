package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateUserRequest {
  @NotBlank private String email;
  @NotBlank private String password;
  private String displayName;

  public CreateUserRequest email(String email) {
    setEmail(email);
    return this;
  }

  public CreateUserRequest password(String password) {
    setPassword(password);
    return this;
  }

  public CreateUserRequest displayName(String displayName) {
    setDisplayName(displayName);
    return this;
  }
}
