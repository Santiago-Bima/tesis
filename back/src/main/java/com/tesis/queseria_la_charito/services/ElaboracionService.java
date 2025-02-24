package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.ElaboracionRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.*;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.ElaboracionResponse;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.InformeElaboracionResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ElaboracionService {
  List<ElaboracionResponse> getAll(String username, LocalDate fechaInicio, LocalDate fechaFin, Long idProducto);
  ElaboracionResponse getById(String username, String id);
  ElaboracionResponse post(ElaboracionRequest elaboracionRequest);
  ElaboracionResponse updateCortes(DetalleCorteRequest detalleCorteRequest, String idElaboracion) throws Exception;
  ElaboracionResponse updateEmbolsado(LocalDate fechaEmbolsado, String idElaboracion) throws Exception;
  ElaboracionResponse updateMaduracion(MaduracionRequest maduracionRequest, String idElaboracion) throws Exception;
  ElaboracionResponse updatePintado(LocalDate fechaPintado, String idElaboracion) throws Exception;
  ElaboracionResponse updateControl(ControlCalidadRequest controlCalidadRequest, String idElaboracion) throws Exception;
  ElaboracionResponse deleteElaboracion(String id);
  InformeElaboracionResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin);
}