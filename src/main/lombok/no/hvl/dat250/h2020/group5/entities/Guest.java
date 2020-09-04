package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity(name="Guest")
public class Guest extends Voter {

}
