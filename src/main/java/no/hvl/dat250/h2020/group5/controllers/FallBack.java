package no.hvl.dat250.h2020.group5.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBack {

  @GetMapping("/")
  public String fallBack() {
    return "Hello!";
  }
}
