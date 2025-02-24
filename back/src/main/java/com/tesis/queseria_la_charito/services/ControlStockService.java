package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.controlStock.ControlStockRequest;
import com.tesis.queseria_la_charito.dtos.response.controlStock.CantidadesEsperadasResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.ControlStockResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ControlStockService {
  List<ControlStockResponse> getAll(boolean validate);
  ControlStockResponse post(ControlStockRequest data);
  CantidadesEsperadasResponse getEsperado();
}
