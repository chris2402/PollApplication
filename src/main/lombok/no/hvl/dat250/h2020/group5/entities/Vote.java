package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="voter_id")
    private Voter voter;

    @JoinColumn(name="poll_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Poll poll;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;

}
