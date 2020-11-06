package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Voter {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "pg-uuid")
  protected UUID id;

  @OneToMany(
      mappedBy = "voter",
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonManagedReference
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  protected List<Vote> votes = new ArrayList<>();

  @Column(length = 40)
  protected String displayName;

  public void addVoteAndSetThisVoterInVote(Vote vote) {
    this.votes.add(vote);
    vote.setVoterOnlyOnVoteSide(this);
  }
}
