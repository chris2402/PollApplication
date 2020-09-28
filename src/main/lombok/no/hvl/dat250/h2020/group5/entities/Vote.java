package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.hvl.dat250.h2020.group5.enums.AnswerType;

import javax.persistence.*;

@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "voter_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Voter voter;

    @JoinColumn(name = "poll_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @JsonBackReference(value = "votes")
    private Poll poll;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;
}
