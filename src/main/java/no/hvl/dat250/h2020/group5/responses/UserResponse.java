package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.User;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String username;
  private Boolean isAdmin;

  public UserResponse(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.isAdmin = user.getIsAdmin();
  }
}
