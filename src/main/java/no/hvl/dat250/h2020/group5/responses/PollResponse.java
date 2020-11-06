package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class PollResponse {

  private Long id;
  private String pollName;
  private String question;
  private Date startTime;
  private Integer pollDuration;
  private PollVisibilityType visibilityType;
  private String owner;
  private List<String> allowedVoters;

  public PollResponse(Poll poll) {
    this.id = poll.getId();
    this.pollDuration = poll.getPollDuration();
    this.pollName = poll.getName();
    this.question = poll.getQuestion();
    this.startTime = poll.getStartTime();
    this.visibilityType = poll.getVisibilityType();
    this.owner = poll.getPollOwner().getEmail();
    if (poll.getVisibilityType().equals(PollVisibilityType.PRIVATE)
        && poll.getAllowedVoters() != null
        && !poll.getAllowedVoters().isEmpty()) {
      allowedVoters =
          poll.getAllowedVoters().stream().map(User::getEmail).collect(Collectors.toList());
    }
  }
}
