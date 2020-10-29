package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Voter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class VoterResponse {
  List<String> roles;
  private Long id;
  private String username;
  private String displayName;

  public VoterResponse(Voter voter) {
    this.id = voter.getId();
    this.username = voter.getUsername();
    this.displayName = voter.getDisplayName();
  }

  public VoterResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
