package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;

@Getter
@Setter
@AllArgsConstructor
public class VotingDeviceResponse {

  private Long id;
  private String username;
  private String jwt;

  public VotingDeviceResponse(VotingDevice votingDevice) {
    this.id = votingDevice.getId();
    this.username = votingDevice.getUsername();
  }
}
