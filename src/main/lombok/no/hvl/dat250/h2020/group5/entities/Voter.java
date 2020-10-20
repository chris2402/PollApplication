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
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Voter {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Column(length = 40)
  @EqualsAndHashCode.Include
  protected String username;

  @OneToMany(
      mappedBy = "voter",
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonManagedReference
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  protected List<Vote> votes = new ArrayList<>();

  private String password;

  public void addVoteAndSetThisVoterInVote(Vote vote) {
    this.votes.add(vote);
    vote.setVoterOnlyOnVoteSide(this);
  }
}
