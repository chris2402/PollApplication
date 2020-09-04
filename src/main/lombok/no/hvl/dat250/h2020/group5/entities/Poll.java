package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
public class Poll {

    // TODO : Design/Implement own Generation type for Poll PIN - https://thorben-janssen.com/custom-sequence-based-idgenerator/
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String name;

    private String question;

    private String alternativeA;
    private String alternativeB;

    @ManyToOne(fetch = FetchType.LAZY)
    private User pollOwner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voter", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

}
