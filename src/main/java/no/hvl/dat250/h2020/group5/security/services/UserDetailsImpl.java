package no.hvl.dat250.h2020.group5.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
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

  public static UserDetailsImpl build(User user) {

    List<GrantedAuthority> authorities =
        Collections.singletonList(
            new SimpleGrantedAuthority(
                user.getIsAdmin() ? Roles.ADMIN.toString() : Roles.USER.toString()));

    return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(), authorities);
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
