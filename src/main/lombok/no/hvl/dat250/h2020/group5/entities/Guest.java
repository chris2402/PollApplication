package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Guest extends Voter {
  public Guest displayName(String name) {
    setDisplayName(name);
    return this;
  }
}
