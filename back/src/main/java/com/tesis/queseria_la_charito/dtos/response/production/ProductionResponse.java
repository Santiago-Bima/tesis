package com.tesis.queseria_la_charito.dtos.response.production;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchResponse;
import com.tesis.queseria_la_charito.dtos.response.UserResponse;
import com.tesis.queseria_la_charito.dtos.response.formula.FormulaResponse;
import com.tesis.queseria_la_charito.dtos.response.production.processes.QualityControlResponse;
import com.tesis.queseria_la_charito.dtos.response.production.processes.CutDetailResponse;
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
public class ProductionResponse {
  @Description("id de elaboracion")
  @JsonProperty("id")
  private String id;

  @JsonProperty("usuario")
  private UserResponse responsible;

  @Description("fecha")
  @JsonProperty("fecha")
  private LocalDate date;

  @Description("cantidad de leche")
  @JsonProperty("cantidadLeche")
  private Integer milkQuantity;

  @Description("lote")
  @JsonProperty("lote")
  private BatchResponse batch;

  @Description("formula")
  @JsonProperty("formula")
  private FormulaResponse formula;

  @Description("tiempo de salado")
  @JsonProperty("tiempoSalado")
  private Integer sauteedTime;

  @Description("fecha de entrada a cámara de maduración")
  @JsonProperty("fechaEntradaMaduracion")
  private LocalDate maturationStartIssued;

  @Description("fecha de salida de cámara de maduración")
  @JsonProperty("fechaSalidaMaduracion")
  private LocalDate maturationExitIssued;

  @Description("fecha de embolsado")
  @JsonProperty("fechaEmbolsado")
  private LocalDate packagingDate;

  @Description("fecha de pintado")
  @JsonProperty("fechaPintado")
  private LocalDate paintingDate;

  //  Procesos

  @Description("cortes")
  @JsonProperty("detalleCorte")
  private CutDetailResponse cutDetail;

  @Description("proceso de control")
  @JsonProperty("control")
  private QualityControlResponse qualityControl;
}
