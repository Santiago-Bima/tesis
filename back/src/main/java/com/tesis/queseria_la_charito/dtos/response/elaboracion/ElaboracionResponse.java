package com.tesis.queseria_la_charito.dtos.response.elaboracion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import com.tesis.queseria_la_charito.dtos.response.UsuarioResponse;
import com.tesis.queseria_la_charito.dtos.response.formula.FormulaResponse;
import com.tesis.queseria_la_charito.dtos.response.proceso.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElaboracionResponse {
  @Description("id de elaboracion")
  @JsonProperty("id")
  private String id;

  @JsonProperty("usuario")
  private UsuarioResponse usuario;

  @Description("fecha")
  @JsonProperty("fecha")
  private LocalDate fecha;

  @Description("cantidad de leche")
  @JsonProperty("cantidadLeche")
  private Integer cantidadLeche;

  @Description("lote")
  @JsonProperty("lote")
  private LoteResponse lote;

  @Description("formula")
  @JsonProperty("formula")
  private FormulaResponse formula;

  @Description("tiempo de salado")
  @JsonProperty("tiempoSalado")
  private Integer tiempoSalado;

  @Description("fecha de entrada a cámara de maduración")
  @JsonProperty("fechaEntradaMaduracion")
  private LocalDate fechaEntradaMaduracion;

  @Description("fecha de salida de cámara de maduración")
  @JsonProperty("fechaSalidaMaduracion")
  private LocalDate fechaSalidaMaduracion;

  @Description("fecha de embolsado")
  @JsonProperty("fechaEmbolsado")
  private LocalDate fechaEmbolsado;

  @Description("fecha de pintado")
  @JsonProperty("fechaPintado")
  private LocalDate fechaPintado;

  //  Procesos

  @Description("cortes")
  @JsonProperty("detalleCorte")
  private DetalleCorteResponse detalleCorte;

  @Description("proceso de control")
  @JsonProperty("control")
  private ControlCalidadResponse controlCalidad;
}
