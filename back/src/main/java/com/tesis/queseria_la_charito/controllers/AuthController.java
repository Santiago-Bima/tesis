package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.dtos.request.LoginRequest;
import com.tesis.queseria_la_charito.dtos.request.RegisterRequest;
import com.tesis.queseria_la_charito.dtos.response.AuthResponse;
import com.tesis.queseria_la_charito.dtos.response.usuario.UsuarioResponse;
import com.tesis.queseria_la_charito.services.Auth.AuthService;
import com.tesis.queseria_la_charito.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("autenticacion")
@RequiredArgsConstructor
public class AuthController {
  @Autowired
  private UsuarioService userService;

  @Autowired
  private final AuthService authService;

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @PutMapping("/usuarios/{username}")
  public UsuarioResponse put(@PathVariable String username, @RequestParam String nombre) { return userService.update(nombre, username); }

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @DeleteMapping("/usuarios/{username}")
  public UsuarioResponse delete(@PathVariable String username) { return userService.delete(username); }

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @GetMapping("/usuarios")
  public List<UsuarioResponse> getAll() { return userService.getAll(); }

//  TODO: Confirmar roles requeridos
  @GetMapping("/usuarios/{id}")
  public UsuarioResponse getById(@PathVariable Long id) { return userService.getById(id); }


//  Rutas de jwt
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @PostMapping("/registro")
  public  ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

}
