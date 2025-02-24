package com.tesis.queseria_la_charito.entities.controlStock;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "controles_insumos")
public class InsumoControlEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_control_insumo", unique = true)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_control")
  private ControlStockEntity controlStock;

  @ManyToOne
  @JoinColumn(name = "id_item")
  private ItemEntity insumo;

  @Column(name = "cantidad")
  private Integer cantidad;

  @Column(name = "tipo")
  private String tipo;
}
