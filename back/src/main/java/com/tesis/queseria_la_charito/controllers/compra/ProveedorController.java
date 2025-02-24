package com.tesis.queseria_la_charito.controllers.compra;

import com.tesis.queseria_la_charito.dtos.request.compra.ProveedorRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ProveedorResponse;
import com.tesis.queseria_la_charito.services.impls.ProveedorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("proveedores")
public class ProveedorController {
  @Autowired
  private ProveedorServiceImpl service;

  @GetMapping("/insumos/{insumo}")
  public List<ProveedorResponse> getAll(@PathVariable Long insumo) { return service.getAll(insumo); }

  @GetMapping("/{id}")
  public ProveedorResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PostMapping("")
  public ProveedorResponse post(@RequestBody ProveedorRequest proveedorRequest) { return service.post(proveedorRequest); }

  @PutMapping("/{id}")
  public ProveedorResponse put(@RequestBody ProveedorRequest proveedorRequest, @PathVariable Long id) { return service.put(proveedorRequest, id); }

  @DeleteMapping("/{id}")
  public ProveedorResponse delete(@PathVariable Long id) { return service.delete(id); }
}
