package com.tesis.queseria_la_charito.dtos.request.purchase;

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
public class ProviderRequest {
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
  private Long phone;

  @JsonProperty("idInsumo")
  private Long idSupply;

  @JsonProperty("insumo")
  private ItemResponse supply;

  @JsonProperty("cantidadMedida")
  private Integer measurementQuantity;

  @JsonProperty("unidadMedida")
  private String measurementUnit;

  @JsonProperty("costo")
  private Integer cost;
}
