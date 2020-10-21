package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Guest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GuestResponse {

  List<String> roles;
  private Long id;
  private String username;
  private String displayName;

  public GuestResponse(Guest guest) {
    this.id = guest.getId();
    this.username = guest.getUsername();
    this.displayName = guest.getDisplayName();
  }

  public GuestResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
