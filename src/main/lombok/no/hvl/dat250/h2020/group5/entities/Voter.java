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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Voter {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Column(length = 20)
  @EqualsAndHashCode.Include
  protected String username;

  @OneToMany(mappedBy = "voter", fetch = FetchType.LAZY)
  @JsonManagedReference
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  protected List<Vote> votes = new ArrayList<>();

  public void addVote(Vote vote) {
    vote.setVoter(this);
    votes.add(vote);
  }
}
