package com.tesis.queseria_la_charito.dtos.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
  @JsonProperty(namespace = "username")
  private String username;

  @JsonProperty(namespace = "password")
  private String password;
}
