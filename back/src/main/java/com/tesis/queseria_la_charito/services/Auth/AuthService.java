package com.tesis.queseria_la_charito.services.Auth;

import com.tesis.queseria_la_charito.dtos.request.LoginRequest;
import com.tesis.queseria_la_charito.dtos.request.RegisterRequest;
import com.tesis.queseria_la_charito.dtos.response.AuthResponse;
import com.tesis.queseria_la_charito.entities.usuario.RolEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.repositories.usuario.RolRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  @Autowired
  private RolRepository rolRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;


  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDetails user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

    String token = jwtService.getToken(user);
    return AuthResponse.builder()
        .token(token)
        .build();
  }

  public AuthResponse register(RegisterRequest request) {
    RolEntity rol = rolRepository.findByRol(request.getRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    UsuarioEntity user = UsuarioEntity.builder()
        .username(request.getUsername())
        .password(hashedPassword)
        .isDispatching(false)
        .mostrar(true)
        .rol(rol).build();

    usuarioRepository.save(user);

    return AuthResponse.builder().token(jwtService.getToken(user)).build();
  }
}
