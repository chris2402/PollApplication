package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class VotingDeviceResponse {

  List<String> roles;
  private Long id;
  private String username;

  public VotingDeviceResponse(VotingDevice votingDevice) {
    this.id = votingDevice.getId();
    this.username = votingDevice.getUsername();
  }

  public VotingDeviceResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }
}
