package com.tesis.queseria_la_charito.entities.despacho;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "vehiculos")
public class VehiculoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_vehiculo", unique = true)
  private Long id;

  @Column(name = "matricula", unique = true)
  private String matricula;

  @Column(name = "disponible")
  private Boolean disponible;

  @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.REFRESH)
  private List<DespachoEntity> lstDespachos;
}
