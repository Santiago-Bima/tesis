package com.tesis.queseria_la_charito.entities.usuario;

import com.tesis.queseria_la_charito.entities.ElaboracionEntity;
import com.tesis.queseria_la_charito.entities.ModificacionLoteEntity;
import com.tesis.queseria_la_charito.entities.controlStock.ControlStockEntity;
import com.tesis.queseria_la_charito.entities.despacho.DespachoEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rol_id")
  private RolEntity rol;

  @Column(name = "mostrar")
  private Boolean mostrar;

  @Column(name = "isDispatching")
  private Boolean isDispatching;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<ElaboracionEntity> lstElaboraciones;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<ModificacionLoteEntity> lstModificacionesLotes;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<ControlStockEntity> lstControlesStock;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<DespachoEntity> lstDespachos;


}

