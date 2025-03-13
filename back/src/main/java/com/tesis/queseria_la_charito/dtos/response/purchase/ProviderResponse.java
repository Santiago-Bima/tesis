package com.tesis.queseria_la_charito.dtos.response.purchase;

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
public class ProviderResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("nombre")
  private String name;

  @JsonProperty("email")
  private String email;

  @JsonProperty("alias")
  private String alias;

  @JsonProperty("cuit")
  private String cuit;

  @JsonProperty("banco")
  private String bank;

  @JsonProperty("tipoCuenta")
  private String accountType;

  @JsonProperty("telefono")
  private BigInteger phone;

  @JsonProperty("insumo")
  private ItemResponse supply;

  @JsonProperty("unidadMedida")
  private String measurementUnit;

  @JsonProperty("cantidadMedida")
  private Integer measurementQuantity;

  @JsonProperty("costo")
  private Integer cost;
}
