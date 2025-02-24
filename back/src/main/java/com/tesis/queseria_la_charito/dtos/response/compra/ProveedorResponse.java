package com.tesis.queseria_la_charito.dtos.response.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponse {
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
  private BigInteger telefono;

  @JsonProperty("insumo")
  private ItemResponse insumo;

  @JsonProperty("unidadMedida")
  private String unidadMedida;

  @JsonProperty("cantidadMedida")
  private Integer cantidadMedida;

  @JsonProperty("costo")
  private Integer costo;
}
