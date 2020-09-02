package com.app.entites;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Vote {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn()
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn()
    private Voter voter;

}
