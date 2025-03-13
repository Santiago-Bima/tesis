package com.tesis.queseria_la_charito.controllers.dispatch;

import com.tesis.queseria_la_charito.dtos.response.dispatch.VehicleResponse;
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
  List<VehicleResponse> getAll() { return service.getAll(); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  VehicleResponse post(@RequestParam String matricula) { return service.post(matricula); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/{id}")
  VehicleResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @DeleteMapping("/{id}")
  VehicleResponse delete(@PathVariable Long id) { return service.delete(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PutMapping("/{id}")
  VehicleResponse put(@PathVariable Long id) { return service.put(id); }
}
