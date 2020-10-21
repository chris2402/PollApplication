package no.hvl.dat250.h2020.group5.integrationtests.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class LoginUserInTest {
  public void login(
      String username,
      String password,
      String endPoint,
      int port,
      TestRestTemplate testRestTemplate,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + endPoint;
    LoginRequest loginRequest = new LoginRequest().username(username).password(password);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    testRestTemplate.postForEntity(loginUrl, request, String.class);
  }
}
