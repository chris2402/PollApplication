package no.hvl.dat250.h2020.group5.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "APP_USER")
@EqualsAndHashCode(callSuper = true)
public class User extends Voter {

    private String password;

    private Boolean isAdmin = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference(value = "pollOwner")
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "pollOwner",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Setter(value = AccessLevel.NONE)
    private List<Poll> userPolls = new ArrayList<>();
}
