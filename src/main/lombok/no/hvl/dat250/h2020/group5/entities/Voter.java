package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Voter {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Column(length = 20)
  protected String username;

  @OneToMany(mappedBy = "voter", fetch = FetchType.EAGER)
  @JsonManagedReference
  @ToString.Exclude
  protected List<Vote> votes = new ArrayList<>();
}

/*
* Oddmund sin version av Voter - One to One med nullable "User"
*
* @Entity
* @Data
* public class Voter {
*
*   @Id
*   private Long id;
*   @OneToMany(mappedBy = "voter")
*   private List<Vote> votes;
*   @OneToOne(mappedBy = "voter")
*   private User user;
*   public boolean isGuest() {
*       return user == null;
*   }
* }

* */
