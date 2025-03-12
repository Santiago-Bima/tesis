package com.tesis.queseria_la_charito.controllers.despacho;

import com.tesis.queseria_la_charito.dtos.request.despacho.DestinoRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DestinoResponse;
import com.tesis.queseria_la_charito.services.despachos.DestinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinoController {
  @Autowired
  private DestinoService destinoService;


  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("/{id}")
  DestinoResponse getById(@PathVariable Long id) { return destinoService.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("")
  List<DestinoResponse> getAll() { return destinoService.getAll(); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PostMapping("")
  DestinoResponse post(@RequestBody DestinoRequest destinoRequest) { return destinoService.post(destinoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PutMapping("/{id}")
  DestinoResponse put(@RequestBody DestinoRequest destinoRequest, @PathVariable Long id) { return destinoService.put(id, destinoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @DeleteMapping("/{id}")
  DestinoResponse delete(@PathVariable Long id) { return destinoService.delete(id); }
}
