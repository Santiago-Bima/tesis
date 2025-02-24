package com.tesis.queseria_la_charito.controllers.despacho;

import com.tesis.queseria_la_charito.dtos.request.despacho.DestinoRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DestinoResponse;
import com.tesis.queseria_la_charito.services.impls.DestinoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinoController {
  @Autowired
  private DestinoServiceImpl destinoService;

  @GetMapping("/{id}")
  DestinoResponse getById(@PathVariable Long id) { return destinoService.getById(id); }

  @GetMapping("")
  List<DestinoResponse> getAll() { return destinoService.getAll(); }

  @PostMapping("")
  DestinoResponse post(@RequestBody DestinoRequest destinoRequest) { return destinoService.post(destinoRequest); }

  @PutMapping("/{id}")
  DestinoResponse put(@RequestBody DestinoRequest destinoRequest, @PathVariable Long id) { return destinoService.put(id, destinoRequest); }

  @DeleteMapping("/{id}")
  DestinoResponse delete(@PathVariable Long id) { return destinoService.delete(id); }
}
