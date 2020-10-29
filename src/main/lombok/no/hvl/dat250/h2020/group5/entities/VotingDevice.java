package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class VotingDevice extends Voter {

  public VotingDevice displayName(String displayName) {
    setDisplayName(displayName);
    return this;
  }

  public VotingDevice username(String username) {
    setUsername(username);
    return this;
  }

  public VotingDevice password(String password) {
    setPassword(password);
    return this;
  }
}
