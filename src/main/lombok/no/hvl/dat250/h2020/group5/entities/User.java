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

  private String password;

  private Boolean isAdmin = false;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonManagedReference(value = "pollOwner")
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "pollOwner",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Poll> userPolls = new ArrayList<>();

  public User userName(String username) {
    setUsername(username);
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

  public void setPollOwnerAndAddToUserPoll(Poll poll) {
    poll.setOwnerOnlyOnPollSide(this);
    this.userPolls.add(poll);
  }

  public boolean deletePoll(Poll poll) {
    return userPolls.remove(poll);
  }
}
