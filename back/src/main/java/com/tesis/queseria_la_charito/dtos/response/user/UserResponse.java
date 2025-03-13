package com.tesis.queseria_la_charito.dtos.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  @JsonProperty(namespace = "username")
  private String username;

  @JsonProperty(namespace = "rol")
  private String role;

  @JsonProperty(namespace = "isDispatching")
  private boolean isDispatching;
}
