package com.tesis.queseria_la_charito.controllers.purchase;

import com.tesis.queseria_la_charito.dtos.request.compra.ProveedorRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ProveedorResponse;
import com.tesis.queseria_la_charito.services.compras.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("proveedores")
public class ProviderController {
  @Autowired
  private ProveedorService service;

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/insumos/{insumo}")
  public List<ProveedorResponse> getAll(@PathVariable Long insumo) { return service.getAll(insumo); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/{id}")
  public ProveedorResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  public ProveedorResponse post(@RequestBody ProveedorRequest proveedorRequest) { return service.post(proveedorRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PutMapping("/{id}")
  public ProveedorResponse put(@RequestBody ProveedorRequest proveedorRequest, @PathVariable Long id) { return service.put(proveedorRequest, id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @DeleteMapping("/{id}")
  public ProveedorResponse delete(@PathVariable Long id) { return service.delete(id); }
}
