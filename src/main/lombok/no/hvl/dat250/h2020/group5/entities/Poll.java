package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
public class Poll {

    // TODO : Design/Implement own Generation type for Poll PIN - https://thorben-janssen.com/custom-sequence-based-idgenerator/
    @Id
    private String id;

    private String name;

    private String question;

    private Date startTime;
    private Integer pollDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    private User pollOwner;

    @OneToMany(mappedBy = "poll", targetEntity = Vote.class, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Vote> votes = new ArrayList<>();

}
