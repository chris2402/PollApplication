package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer voteA;
    private Integer voteB;

    @ManyToOne(fetch = FetchType.LAZY)
    private Voter voter;

    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;
}
