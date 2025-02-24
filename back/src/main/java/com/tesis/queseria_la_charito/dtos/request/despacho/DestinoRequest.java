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
public class DestinoRequest {
  @JsonProperty("calle")
  private String calle;

  @JsonProperty("numero")
  private Integer numero;

  @JsonProperty("barrio")
  private String barrio;
}
