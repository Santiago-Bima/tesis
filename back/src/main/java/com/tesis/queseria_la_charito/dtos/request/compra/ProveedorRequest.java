package com.tesis.queseria_la_charito.dtos.request.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProveedorRequest {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("nombre")
  private String nombre;

  @JsonProperty("email")
  private String email;

  @JsonProperty("alias")
  private String alias;

  @JsonProperty("cuit")
  private String cuit;

  @JsonProperty("banco")
  private String banco;

  @JsonProperty("tipoCuenta")
  private String tipoCuenta;

  @JsonProperty("telefono")
  private Long telefono;

  @JsonProperty("idInsumo")
  private Long idInsumo;

  @JsonProperty("insumo")
  private ItemResponse insumo;

  @JsonProperty("cantidadMedida")
  private Integer cantidadMedida;

  @JsonProperty("unidadMedida")
  private String unidadMedida;

  @JsonProperty("costo")
  private Integer costo;
}
