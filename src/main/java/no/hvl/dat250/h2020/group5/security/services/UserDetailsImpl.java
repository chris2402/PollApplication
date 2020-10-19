package no.hvl.dat250.h2020.group5.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  @JsonIgnore private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      Long id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(Voter voter) {
    System.out.println("INSTANCE: " + (voter instanceof Guest));

    User user = null;
    if (voter instanceof User) {
      user = (User) voter;
    }
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (voter instanceof Guest) {
      authorities.add(new SimpleGrantedAuthority(Roles.GUEST.toString()));
    }
    if (voter instanceof User) {
      authorities.add(new SimpleGrantedAuthority(Roles.USER.toString()));
    }
    if (voter instanceof User && user.getIsAdmin()) {
      authorities.add(new SimpleGrantedAuthority(Roles.ADMIN.toString()));
    }

    return new UserDetailsImpl(
        voter.getId(), voter.getUsername(), voter.getPassword(), authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
