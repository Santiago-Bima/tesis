package com.tesis.queseria_la_charito.dtos.request.procesosElaboracion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaduracionRequest {
  @JsonProperty("fechaEntrada")
  private LocalDate fechaEntrada;

  @JsonProperty("fechaSalida")
  private LocalDate fechaSalida;
}
