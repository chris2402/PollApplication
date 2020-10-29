package no.hvl.dat250.h2020.group5.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import no.hvl.dat250.h2020.group5.KeyHelper;
import no.hvl.dat250.h2020.group5.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${poll.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    if (StringUtils.isEmpty(userPrincipal.getUsername()))
      throw new IllegalArgumentException("Cannot create JWT Token without username");
    if (userPrincipal.getAuthorities() == null || userPrincipal.getAuthorities().isEmpty())
      throw new IllegalArgumentException("User doesn't have any privileges");

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setId(userPrincipal.getId().toString())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(KeyHelper.getKey())
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(KeyHelper.getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String getIdFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(KeyHelper.getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getId();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(KeyHelper.getKey()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }
}
