package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.enums.Roles;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
  List<String> roles;
  private Long id;
  private String email;
  private Boolean isAdmin;

  public UserResponse(Account account) {
    this.id = account.getId();
    this.email = account.getEmail();
    this.isAdmin = account.getIsAdmin();
    this.roles =
        Collections.singletonList(
            account.getIsAdmin() ? Roles.ADMIN.toString() : Roles.USER.toString());
  }

  public UserResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
