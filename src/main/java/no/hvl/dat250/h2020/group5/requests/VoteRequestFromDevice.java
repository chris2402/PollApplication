package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoteRequestFromDevice {

  private int numberOfYes = 0;
  private int numberOfNo = 0;

  public VoteRequestFromDevice(Long id, int numberOfYes, int numberOfNo) {
    this.numberOfNo = numberOfNo;
    this.numberOfYes = numberOfYes;
  }
}
