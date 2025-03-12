package com.tesis.queseria_la_charito.controllers.despacho;

import com.tesis.queseria_la_charito.dtos.response.despacho.VehiculoResponse;
import com.tesis.queseria_la_charito.services.impls.VehiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vehiculos")
public class VehiculoController {
  @Autowired
  private VehiculoServiceImpl vehiculoService;


  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("")
  List<VehiculoResponse> getAll() { return vehiculoService.getAll(); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  VehiculoResponse post(@RequestParam String matricula) { return vehiculoService.post(matricula); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/{id}")
  VehiculoResponse getById(@PathVariable Long id) { return vehiculoService.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @DeleteMapping("/{id}")
  VehiculoResponse delete(@PathVariable Long id) { return vehiculoService.delete(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PutMapping("/{id}")
  VehiculoResponse put(@PathVariable Long id) { return vehiculoService.put(id); }
}
