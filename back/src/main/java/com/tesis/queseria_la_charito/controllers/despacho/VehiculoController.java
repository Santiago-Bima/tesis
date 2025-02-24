package com.tesis.queseria_la_charito.controllers.despacho;

import com.tesis.queseria_la_charito.dtos.response.despacho.VehiculoResponse;
import com.tesis.queseria_la_charito.services.impls.VehiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vehiculos")
public class VehiculoController {
  @Autowired
  private VehiculoServiceImpl vehiculoService;

  @GetMapping("")
  List<VehiculoResponse> getAll() { return vehiculoService.getAll(); }

  @PostMapping("")
  VehiculoResponse post(@RequestParam String matricula) { return vehiculoService.post(matricula); }

  @GetMapping("/{id}")
  VehiculoResponse getById(@PathVariable Long id) { return vehiculoService.getById(id); }

  @DeleteMapping("/{id}")
  VehiculoResponse delete(@PathVariable Long id) { return vehiculoService.delete(id); }

  @PutMapping("/{id}")
  VehiculoResponse put(@PathVariable Long id) { return vehiculoService.put(id); }
}
