package com.tesis.queseria_la_charito.controllers.dispatch;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DestinationRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.services.despachos.DestinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinationController {
  @Autowired
  private DestinoService service;


  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("/{id}")
  DestinationResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("")
  List<DestinationResponse> getAll() { return service.getAll(); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PostMapping("")
  DestinationResponse post(@RequestBody DestinationRequest destinoRequest) { return service.post(destinoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PutMapping("/{id}")
  DestinationResponse put(@RequestBody DestinationRequest destinoRequest, @PathVariable Long id) { return service.put(id, destinoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @DeleteMapping("/{id}")
  DestinationResponse delete(@PathVariable Long id) { return service.delete(id); }
}
