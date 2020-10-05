package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import no.hvl.dat250.h2020.group5.converters.AlphaNumeric2Long;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Poll {

  private static final int LOWEST_4_DIGIT_BASE36 = 1679616;

  @Id
  @SequenceGenerator(name = "PollID_Sequence", initialValue = LOWEST_4_DIGIT_BASE36)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PollID_Sequence")
  @Convert(converter = AlphaNumeric2Long.class)
  private Long id;

  private String name;

  private String question;

  private Date startTime;
  private Integer pollDuration;

  @Enumerated(EnumType.STRING)
  private PollVisibilityType visibilityType;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonBackReference(value = "pollOwner")
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "pollId")
  @Setter(AccessLevel.PRIVATE)
  private User pollOwner;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "poll",
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  @JsonManagedReference(value = "votes")
  @Setter(AccessLevel.PRIVATE)
  private List<Vote> votes = new ArrayList<>();

  /**
   * @param vote
   * @return True if vote is added to this poll
   */
  public boolean addVoteAndSetThisPollInVote(Vote vote) {
    this.votes.add(vote);
    vote.setPollOnlyOnVoteSide(this);
    return true;
  }

  public void setOwnerAndAddThisPollToOwner(User user) {
    this.pollOwner = user;
    user.getUserPolls().add(this);
  }

  public Poll visibilityType(PollVisibilityType type) {
    this.setVisibilityType(type);
    return this;
  }

  public Poll pollOwner(User owner) {
    setOwnerAndAddThisPollToOwner(owner);
    return this;
  }

  public Poll name(String name) {
    this.name = name;
    return this;
  }

  public Poll question(String question) {
    this.question = question;
    return this;
  }

  public Poll startTime(Date startTime) {
    this.startTime = startTime;
    return this;
  }

  public Poll pollDuration(Integer pollDuration) {
    this.pollDuration = pollDuration;
    return this;
  }

  public void setOwnerOnlyOnPollSide(User user) {
    setPollOwner(user);
  }
}
