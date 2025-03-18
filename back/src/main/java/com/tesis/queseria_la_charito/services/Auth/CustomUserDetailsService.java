package com.tesis.queseria_la_charito.services.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    // Retornar un User con roles desde la base de datos
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.getAuthorities()  // Asegúrate de que getAuthorities() devuelva los roles correctos
    );
  }
}
