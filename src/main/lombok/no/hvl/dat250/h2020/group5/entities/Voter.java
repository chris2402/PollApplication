package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private Long id;

    @Column(length = 20)
    @EqualsAndHashCode.Include
    private String userName;

    @OneToMany(mappedBy = "voter", fetch = FetchType.LAZY)
    private List<Vote> votes = new ArrayList<>();

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