package com.tesis.queseria_la_charito.controllers.purchase;

import com.tesis.queseria_la_charito.dtos.request.purchase.ProviderRequest;
import com.tesis.queseria_la_charito.dtos.response.purchase.ProviderResponse;
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
  public List<ProviderResponse> getAll(@PathVariable Long insumo) { return service.getAll(insumo); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("/{id}")
  public ProviderResponse getById(@PathVariable Long id) { return service.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  public ProviderResponse post(@RequestBody ProviderRequest proveedorRequest) { return service.post(proveedorRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PutMapping("/{id}")
  public ProviderResponse put(@RequestBody ProviderRequest proveedorRequest, @PathVariable Long id) { return service.put(proveedorRequest, id); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @DeleteMapping("/{id}")
  public ProviderResponse delete(@PathVariable Long id) { return service.delete(id); }
}
