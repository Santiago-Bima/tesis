package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.compra.ComprobanteCompraRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ComprobanteCompraResponse;
import com.tesis.queseria_la_charito.dtos.response.compra.InformeCompraResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface CompraService {
  List<ComprobanteCompraResponse> getAll(LocalDate fecha);
  ComprobanteCompraResponse post(ComprobanteCompraRequest comprobante);
  List<InformeCompraResponse> generateInforme(LocalDate fechaInicio, LocalDate fechaFin);
}
