package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.models.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @GetMapping()
  List<Role> get() { return Arrays.asList(Role.values()) ;}
}
