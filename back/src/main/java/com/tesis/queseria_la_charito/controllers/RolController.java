package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.models.Roles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {
  @GetMapping()
  List<Roles> get() { return Arrays.asList(Roles.values()) ;}
}
