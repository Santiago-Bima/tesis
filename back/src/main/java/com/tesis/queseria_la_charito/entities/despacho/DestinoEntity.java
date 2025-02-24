package com.tesis.queseria_la_charito.entities.despacho;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "destinos")
public class DestinoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_destino", unique = true)
  private Long id;

  @Column(name = "calle")
  private String calle;

  @Column(name = "numero")
  private Integer numero;

  @Column(name = "barrio")
  private String barrio;

  @OneToMany(mappedBy = "destino", cascade = CascadeType.REFRESH)
  List<DespachoEntity> lstDespachos;
}
