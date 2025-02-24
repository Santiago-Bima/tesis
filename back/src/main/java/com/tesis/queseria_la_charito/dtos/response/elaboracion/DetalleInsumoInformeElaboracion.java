package com.tesis.queseria_la_charito.dtos.response.elaboracion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleInsumoInformeElaboracion {
  @JsonProperty("insumo")
  private ItemResponse insumo;

  @JsonProperty("total")
  private Integer total;
}
