package com.tesis.queseria_la_charito.entities.despacho;

import com.tesis.queseria_la_charito.entities.LoteEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_despacho")
public class DetalleDespachoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle_despacho")
  private Long id;

//  TODO: Se puede mejorar el como cuenta las cantidades, ya que un lote solo tiene 1 tipo de corte
  @ManyToOne
  @JoinColumn(name = "id_lote")
  private LoteEntity lote;

  @Column(name = "cantidad_enteros")
  private Integer cantidadEnteros;

  @Column(name = "cantidad_medios")
  private Integer cantidadMedios;

  @Column(name = "cantidad_cuartos")
  private Integer cantidadCuartos;

  @ManyToOne
  @JoinColumn(name = "id_despacho")
  private DespachoEntity despacho;
}
