package com.tesis.queseria_la_charito.dtos.response.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinoResponse {
  @Description("id del destino")
  @JsonProperty("id")
  private Long id;

  @Description("calle")
  @JsonProperty("calle")
  private String calle;

  @Description("numero de calle")
  @JsonProperty("numero")
  private Integer numero;

  @Description("barrio")
  @JsonProperty("barrio")
  private String barrio;
}
