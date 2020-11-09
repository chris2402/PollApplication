package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "APP_USER")
@EqualsAndHashCode(callSuper = true)
public class User extends Voter {

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VotingDevice> votingDevices = new ArrayList<>();

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonManagedReference(value = "pollOwner")
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "pollOwner",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Poll> userPolls = new ArrayList<>();

  private String email;
  private String password;
  private Boolean isAdmin = false;

  public void setPollOwnerAndAddToUserPoll(Poll poll) {
    poll.setPollOwnerOnlyOnPollSide(this);
    this.userPolls.add(poll);
  }

  public void detachPoll(Poll poll) {
    poll.setPollOwnerOnlyOnPollSide(null);
    userPolls.remove(poll);
  }

  public User displayName(String name) {
    setDisplayName(name);
    return this;
  }

  public User email(String email) {
    setEmail(email);
    return this;
  }

  public User password(String password) {
    setPassword(password);
    return this;
  }

  public User admin(boolean isAdmin) {
    setIsAdmin(isAdmin);
    return this;
  }
}
