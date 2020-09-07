package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User deviceUser;

}
