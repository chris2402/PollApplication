package no.hvl.dat250.h2020.group5.entities;

import enums.PollVisibilityType;
import lombok.Data;
import no.hvl.dat250.h2020.group5.converters.AlphaNumeric2Long;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
public class Poll {


    private final static int LOWEST_4_DIGIT_BASE36 = 1679616;

    @Id
    @SequenceGenerator(name="PollID_Sequence", initialValue = LOWEST_4_DIGIT_BASE36)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PollID_Sequence")
    @Convert(converter = AlphaNumeric2Long.class)
    private String id;

    private String name;

    private String question;

    private Date startTime;
    private Integer pollDuration;

    @Enumerated(EnumType.STRING)
    private PollVisibilityType visibilityType;

    @ManyToOne(fetch = FetchType.LAZY)
    private User pollOwner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

}
