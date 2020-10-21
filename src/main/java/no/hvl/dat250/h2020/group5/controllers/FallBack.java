package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.service.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBack {

  @Autowired
  Publisher publisher;

  @GetMapping("/")
  public String fallBack() {
    publisher.send("Hello!");
    return "Hello!";
  }
}
