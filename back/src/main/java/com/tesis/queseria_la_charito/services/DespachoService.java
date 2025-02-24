package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoRequest;
import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DespachoResponse;
import com.tesis.queseria_la_charito.dtos.response.despacho.InformeDespachoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface DespachoService {
  List<DespachoResponse> getAll(LocalDate fecha, Long destinoId);
  DespachoResponse getById(Long id);
  DespachoResponse post(DespachoRequest despachoRequest);
  DespachoResponse put(DespachoUpdateRequest despachoRequest, Long id);
  DespachoResponse delete(Long id);
  InformeDespachoResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin);
  List<DespachoResponse> getByUser(String username);
  DespachoResponse changeEstado(Long id);
}
