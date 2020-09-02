package com.app.entites;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class PollVote {

    @Id
    private Long id;

    private Date deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn()
    private Poll poll;

    @OneToMany
    @PrimaryKeyJoinColumn()
    private List<Vote> votes;

}
