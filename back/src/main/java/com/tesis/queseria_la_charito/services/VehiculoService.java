package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.response.despacho.VehiculoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VehiculoService {
  List<VehiculoResponse> getAll();
  VehiculoResponse getById(Long id);
  VehiculoResponse post(String matricula);
  VehiculoResponse delete(Long id);
  VehiculoResponse put(Long id);
}
