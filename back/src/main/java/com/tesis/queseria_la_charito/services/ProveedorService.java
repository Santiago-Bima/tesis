package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.compra.ProveedorRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ProveedorResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProveedorService {
  List<ProveedorResponse> getAll(Long idInsumo);
  ProveedorResponse getById(Long id);
  ProveedorResponse post(ProveedorRequest proveedor);
  ProveedorResponse put(ProveedorRequest proveedor, Long id);
  ProveedorResponse delete(Long id);
}
