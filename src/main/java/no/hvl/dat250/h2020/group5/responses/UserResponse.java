package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.Roles;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
  List<String> roles;
  private Long id;
  private String username;
  private Boolean isAdmin;

  public UserResponse(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.isAdmin = user.getIsAdmin();
    this.roles = Arrays.asList(user.getIsAdmin() ? Roles.ADMIN.toString() : Roles.USER.toString());
  }

  public UserResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
