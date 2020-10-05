package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
  private Voter voter;

  @JoinColumn(name = "poll_id")
  @ManyToOne(fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @JsonBackReference(value = "votes")
  private Poll poll;

  @Enumerated(EnumType.STRING)
  private AnswerType answer;

  public Vote answer(AnswerType answer) {
    this.setAnswer(answer);
    return this;
  }

  /**
   * Check if poll already have the vote to avoid circular dependency.
   *
   * @param poll
   * @return true if vote is added to this poll
   */
  public void addPoll(Poll poll) {
    this.poll = poll;
    poll.getVotes().add(this);
  }

  /**
   * Check if voter already have the vote to avoid circular dependency.
   *
   * @param voter
   * @return true if vote is added to this voter or if voter is set to null
   */
  public boolean setVoter(Voter voter) {
    if (voter == null) {
      this.voter = null;
      return true;
    }
    if (voter.getVotes().contains(this)) {
      return false;
    }
    voter.addVote(this);
    this.voter = voter;
    return true;
  }
}
