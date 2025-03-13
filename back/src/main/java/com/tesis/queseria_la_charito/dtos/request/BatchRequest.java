package com.tesis.queseria_la_charito.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class BatchRequest {
    @JsonProperty("fecha")
    private LocalDate date;

    @JsonProperty("unidades")
    private Integer quantity;

    @JsonProperty("motivos")
    private String reasons;

    @JsonProperty("usuario")
    private String responsible;
}
