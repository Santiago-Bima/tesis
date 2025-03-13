package com.tesis.queseria_la_charito.dtos.request.dispatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchUpdateRequest {
  @JsonProperty("idVehiculo")
  private Long idVehicle;

  @JsonProperty("idDestino")
  private Long idDestination;

  @JsonProperty("usuario")
  private String responsible;
}
