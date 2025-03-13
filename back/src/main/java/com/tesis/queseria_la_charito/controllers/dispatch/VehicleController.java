package com.tesis.queseria_la_charito.controllers.dispatch;

import com.tesis.queseria_la_charito.dtos.response.despacho.VehiculoResponse;
import com.tesis.queseria_la_charito.services.despachos.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vehiculos")
public class VehicleController {
  @Autowired
  private VehiculoService service;


  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("")
  List<VehiculoResponse> getAll() { return service.getAll(); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  VehiculoResponse post(@RequestParam String matricula) { return service.post(matricula); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/{id}")
  VehiculoResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @DeleteMapping("/{id}")
  VehiculoResponse delete(@PathVariable Long id) { return service.delete(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PutMapping("/{id}")
  VehiculoResponse put(@PathVariable Long id) { return service.put(id); }
}
