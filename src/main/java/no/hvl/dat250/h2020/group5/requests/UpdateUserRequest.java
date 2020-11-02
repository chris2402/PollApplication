package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class UpdateUserRequest {

  private String email;

  private String oldPassword;

  private String newPassword;

  private Boolean isAdmin;

  public UpdateUserRequest email(String email) {
    setEmail(email);
    return this;
  }

  public UpdateUserRequest oldPassword(String oldPassword) {
    setOldPassword(oldPassword);
    return this;
  }

  public UpdateUserRequest newPassword(String newPassword) {
    setNewPassword(newPassword);
    return this;
  }

  public UpdateUserRequest isAdmin(Boolean isAdmin) {
    setIsAdmin(isAdmin);
    return this;
  }
}
