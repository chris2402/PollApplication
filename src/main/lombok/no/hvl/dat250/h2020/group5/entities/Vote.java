package no.hvl.dat250.h2020.group5.entities;

import no.hvl.dat250.h2020.group5.enums.AnswerType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="voter_id")
    @EqualsAndHashCode.Exclude
    private Voter voter;

    @JoinColumn(name="poll_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    private Poll poll;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;

}
