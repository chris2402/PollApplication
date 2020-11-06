package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class VotingDeviceResponse {

  private UUID id;
  private String username;
  private String displayName;

  public VotingDeviceResponse(VotingDevice votingDevice) {
    this.id = votingDevice.getId();
    this.displayName = votingDevice.getDisplayName();
  }
}
