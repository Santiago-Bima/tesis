package com.tesis.queseria_la_charito.dtos.response.dispatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("matricula")
  private String plate;

  @JsonProperty("disponible")
  private Boolean disponible;
}
