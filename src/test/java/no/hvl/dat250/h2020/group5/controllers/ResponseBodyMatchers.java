package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

public class ResponseBodyMatchers {

  private final ObjectMapper objectMapper = new ObjectMapper();

  static ResponseBodyMatchers responseBody() {
    return new ResponseBodyMatchers();
  }

  /**
   * Method for easily checking the response in the controller unit-testing
   *
   * @param expectedObject, The object you expect to return from MockMvc.
   * @return
   */
  public ResultMatcher containsObjectAsJson(Object expectedObject) {
    return mvcResult -> {
      String actualResponse = mvcResult.getResponse().getContentAsString();
      Assertions.assertEquals(objectMapper.writeValueAsString(expectedObject), actualResponse);
    };
  }
}
