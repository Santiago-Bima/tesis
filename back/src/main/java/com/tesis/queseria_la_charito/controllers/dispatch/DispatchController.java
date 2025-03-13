package com.tesis.queseria_la_charito.controllers.dispatch;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchRequest;
import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DespachoResponse;
import com.tesis.queseria_la_charito.dtos.response.despacho.InformeDespachoResponse;
import com.tesis.queseria_la_charito.services.despachos.DespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/despachos")
public class DispatchController {
  @Autowired
  private DespachoService service;

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("")
  List<DespachoResponse> getAll(@RequestParam(required = false) LocalDate fecha, @RequestParam Long destinoId) { return service.getAll(fecha, destinoId); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/{id}")
  DespachoResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PostMapping("")
  DespachoResponse post(@RequestBody DispatchRequest despachoRequest) { return service.post(despachoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PutMapping("/{id}")
  DespachoResponse put(@PathVariable Long id, @RequestBody DispatchUpdateRequest despachoUpdateRequest) { return service.put(despachoUpdateRequest, id); }

  @PreAuthorize("hasAnyRole('ROLE_Subgerente', 'ROLE_Operario'")
  @DeleteMapping("/{id}")
  DespachoResponse delete(@PathVariable Long id) { return service.delete(id); }

  @PreAuthorize("hasAnyRole('ROLE_Gerente'")
  @GetMapping("/informes")
  InformeDespachoResponse generateReport(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return service.generateInforme(fechaInicio, fechaFin); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/mi-despacho/{username}")
  List<DespachoResponse> getByUser(@PathVariable String username) { return service.getByUser(username); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/cambiar-estado/{id}")
  DespachoResponse changeStatus(@PathVariable Long id) { return service.changeEstado(id); }
}
