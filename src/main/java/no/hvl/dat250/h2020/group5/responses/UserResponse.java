package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.Roles;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
  List<String> roles;
  private UUID id;
  private String email;
  private String displayName;
  private Boolean isAdmin;

  public UserResponse(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.displayName = user.getDisplayName();
    this.isAdmin = user.getIsAdmin();
    this.roles =
        Collections.singletonList(
            user.getIsAdmin() ? Roles.ADMIN.toString() : Roles.USER.toString());
  }

  public UserResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
