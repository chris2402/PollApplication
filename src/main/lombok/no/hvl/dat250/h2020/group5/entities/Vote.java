package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;

import javax.persistence.*;

@Data
@Entity
public class Vote {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "voter_id")
  @EqualsAndHashCode.Exclude
  @JsonBackReference
  @Setter(AccessLevel.PRIVATE)
  private Voter voter;

  @JoinColumn(name = "poll_id")
  @ManyToOne(fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @JsonBackReference(value = "votes")
  @Setter(AccessLevel.PRIVATE)
  private Poll poll;

  @Enumerated(EnumType.STRING)
  private AnswerType answer;

  public Vote answer(AnswerType answer) {
    this.setAnswer(answer);
    return this;
  }

  /** @param poll if poll is to be deleted it will set poll to null */
  public void setPollAndAddThisVoteToPoll(Poll poll) {
    if (poll == null) {
      this.poll.getVotes().remove(this);
      this.poll = null;
      return;
    }
    this.poll = poll;
    poll.getVotes().add(this);
  }

  /**
   * @param voter if vote is to be deleted it will set voter to null.
   * @return true if voter is set.
   */
  public boolean setVoterAndAddThisVoteToVoter(Voter voter) {
    if (voter == null) {
      this.voter = null;
      return true;
    }
    this.voter = voter;
    voter.getVotes().add(this);
    return true;
  }

  public void setPollOnlyOnVoteSide(Poll poll) {
    setPoll(poll);
  }

  public void setVoterOnlyOnVoteSide(Voter voter) {
    setVoter(voter);
  }
}
