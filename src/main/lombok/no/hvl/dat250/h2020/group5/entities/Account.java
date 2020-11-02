package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER_ACCOUNT")
public class Account {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  private String email;
  private String password;
  private Boolean isAdmin = false;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToOne(
      mappedBy = "account",
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      optional = false)
  private User user;

  public void setUserAndAddThisToUser(User user) {
    setUser(user);
    user.setAccount(this);
  }

  public Account email(String email) {
    setEmail(email);
    return this;
  }

  public Account password(String password) {
    setPassword(password);
    return this;
  }

  public Account admin(boolean isAdmin) {
    setIsAdmin(isAdmin);
    return this;
  }
}
