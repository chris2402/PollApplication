package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.User;

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
  }

  public UserResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
