package no.hvl.dat250.h2020.group5.security.jwt;

import no.hvl.dat250.h2020.group5.security.services.UserDetailsImpl;
import no.hvl.dat250.h2020.group5.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AuthTokenFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
  @Autowired private JwtUtils jwtUtils;
  @Autowired private UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = parseJwtFromCookie(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String email = jwtUtils.getUserNameFromJwtToken(jwt);

        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }
    Optional<Cookie> maybeCookie =
        Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals("auth"))
            .findFirst();
    return maybeCookie.map(Cookie::getValue).orElse(null);
  }
}
