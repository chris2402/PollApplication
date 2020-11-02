package no.hvl.dat250.h2020.group5.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import no.hvl.dat250.h2020.group5.entities.Account;
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

  private Long id;
  private UUID userId;
  private String username;
  @JsonIgnore private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      Long id,
      UUID userId,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(Account account) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(Roles.USER.toString()));
    if (account.getIsAdmin()) {
      authorities.add(new SimpleGrantedAuthority(Roles.ADMIN.toString()));
    }

    return new UserDetailsImpl(
        account.getId(),
        account.getUser().getId(),
        account.getEmail(),
        account.getPassword(),
        authorities);
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
