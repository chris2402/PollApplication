package no.hvl.dat250.h2020.group5.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class VoteId implements Serializable {

    @EqualsAndHashCode.Include
    private String poll;

    @EqualsAndHashCode.Include
    private String voter;
}
