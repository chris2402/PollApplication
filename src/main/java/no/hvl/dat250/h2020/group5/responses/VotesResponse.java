package no.hvl.dat250.h2020.group5.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotesResponse {
  Integer yes;
  Integer no;

  public VotesResponse yes(int numberOfYes) {
    this.setYes(numberOfYes);
    return this;
  }

  public VotesResponse no(int numberOfNo) {
    this.setNo(numberOfNo);
    return this;
  }
}
