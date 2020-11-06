package no.hvl.dat250.h2020.group5.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final String username;
  @JsonIgnore private final String password;

  private final Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      UUID id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = email;
    this.password = password;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(Roles.USER.toString()));
    if (user.getIsAdmin()) {
      authorities.add(new SimpleGrantedAuthority(Roles.ADMIN.toString()));
    }

    return new UserDetailsImpl(user.getId(), user.getEmail(), user.getPassword(), authorities);
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
