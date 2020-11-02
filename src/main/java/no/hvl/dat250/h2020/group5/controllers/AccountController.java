package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('USER')")
@RestController
@RequestMapping("/users")
public class AccountController {

  private final AccountService accountService;
  private final ExtractFromAuth extractFromAuth;

  public AccountController(AccountService accountService, ExtractFromAuth extractFromAuth) {
    this.accountService = accountService;
    this.extractFromAuth = extractFromAuth;
  }

  @GetMapping("/me")
  public UserResponse getMe(Authentication authentication) {
    UserResponse user = accountService.getAccount(extractFromAuth.accountId(authentication));
    user.setRoles(
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    return user;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<UserResponse> getAccounts() {
    return accountService.getAllAccounts();
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public UserResponse getAccount(@PathVariable Long id) {
    return accountService.getAccount(id);
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @PatchMapping("/{id}")
  public Boolean updateAccount(
      @PathVariable Long id,
      @RequestBody UpdateUserRequest updateUserRequest,
      Authentication authentication) {
    return accountService.updateAccount(
        id, updateUserRequest, extractFromAuth.accountId(authentication));
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public Boolean deleteAccount(@PathVariable Long id) {
    return accountService.deleteAccount(id);
  }
}
