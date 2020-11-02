package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Setter
@Getter
public class VoteRequest {
  UUID id;
  @NotBlank String vote;

  public VoteRequest id(UUID id) {
    setId(id);
    return this;
  }

  public VoteRequest vote(String vote) {
    setVote(vote);
    return this;
  }
}
