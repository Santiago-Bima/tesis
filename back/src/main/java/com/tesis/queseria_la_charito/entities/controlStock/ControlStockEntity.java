package com.tesis.queseria_la_charito.entities.controlStock;

import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "controles_stock")
public class ControlStockEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_control", unique = true)
  private Long id;

  @Column(name = "fecha")
  private LocalDate fecha;

  @Column(name = "cantidad_enteros_esperada")
  private Integer cantidadEnterosEsperada;

  @Column(name = "cantidad_enteros_obtenida")
  private Integer cantidadEnterosObtenida;

  @Column(name = "cantidad_medios_esperada")
  private Integer cantidadMediosEsperada;

  @Column(name = "cantidad_medios_obtenida")
  private Integer cantidadMediosObtenida;

  @Column(name = "cantidad_cuartos_esperada")
  private Integer cantidadCuartosEsperada;

  @Column(name = "cantidad_cuartos_obtenida")
  private Integer cantidadCuartosObtenida;

  @Column(name = "nuevo")
  private boolean nuevo;

  @OneToMany(mappedBy = "controlStock", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InsumoControlEntity> controlesInsumosEsperados;

  @OneToMany(mappedBy = "controlStock", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InsumoControlEntity> controlesInsumosObtenidos;

  @Column(name = "observaciones")
  private String observaciones;

  @ManyToOne
  @JoinColumn(name = "username")
  private UsuarioEntity usuario;
}
