package com.tesis.queseria_la_charito.entities.despacho;

import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "despachos")
public class DespachoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_despachos", unique = true)
  private Long id;

  @Column(name = "fecha")
  private LocalDate fecha;

  @Column(name = "queso")
  private String queso;

  @Column(name = "cantidad_total")
  private Integer cantidadTotal;

  @Column(name = "estado")
  private String estado;

  @ManyToOne
  @JoinColumn(name = "id_destino")
  private DestinoEntity destino;

  @ManyToOne
  @JoinColumn(name = "id_vehiculo")
  private VehiculoEntity vehiculo;

  @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL)
  private List<DetalleDespachoEntity> lstDetallesDespacho;

  @ManyToOne
  @JoinColumn(name = "username")
  private UsuarioEntity usuario;
}
