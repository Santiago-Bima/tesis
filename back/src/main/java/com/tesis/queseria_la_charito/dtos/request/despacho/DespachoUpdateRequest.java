package com.tesis.queseria_la_charito.dtos.request.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DespachoUpdateRequest {
  @JsonProperty("idVehiculo")
  private Long idVehiculo;

  @JsonProperty("idDestino")
  private Long idDestino;

  @JsonProperty("usuario")
  private String usuario;
}
