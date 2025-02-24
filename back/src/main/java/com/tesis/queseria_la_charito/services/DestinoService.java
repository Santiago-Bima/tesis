package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.despacho.DestinoRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DestinoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DestinoService {
  DestinoResponse getById(Long id);
  List<DestinoResponse> getAll();
  DestinoResponse put(Long id, DestinoRequest destinoRequest);
  DestinoResponse post(DestinoRequest destinoRequest);
  DestinoResponse delete(Long id);
}
