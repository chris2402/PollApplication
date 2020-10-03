package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

public class ResponseBodyMatchers {

  private final ObjectMapper objectMapper = new ObjectMapper();

  static ResponseBodyMatchers responseBody() {
    return new ResponseBodyMatchers();
  }

  public <T> ResultMatcher containsObjectAsJson(Object expectedObject) {
    return mvcResult -> {
      String actualResponse = mvcResult.getResponse().getContentAsString();
      Assertions.assertEquals(objectMapper.writeValueAsString(expectedObject), actualResponse);
    };
  }
}
