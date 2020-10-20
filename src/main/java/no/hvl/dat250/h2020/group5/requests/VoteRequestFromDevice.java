package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class VoteRequestFromDevice {

  private int numberOfYes = 0;
  private int numberOfNo = 0;
  private Long deviceId;

  public VoteRequestFromDevice(Long deviceId, int numberOfYes, int numberOfNo) {
    this.numberOfNo = numberOfNo;
    this.numberOfYes = numberOfYes;
    this.deviceId = deviceId;
  }

  public List<Vote> getVotes() {
    List<Vote> votes = new ArrayList<>();
    for (int i = 0; i < numberOfYes; i++) {
      votes.add(new Vote().answer(AnswerType.YES));
    }
    for (int i = 0; i < numberOfNo; i++) {
      votes.add(new Vote().answer(AnswerType.NO));
    }
    return votes;
  }
}
