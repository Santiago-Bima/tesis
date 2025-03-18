package com.tesis.queseria_la_charito.entities.dispatch;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "dispatch_details")
public class DispatchDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detail_dispatch")
  private Long id;

//  TODO: Se puede mejorar el como cuenta las cantidades, ya que un lote solo tiene 1 tipo de corte
  @ManyToOne
  @JoinColumn(name = "id_batch")
  private BatchEntity batch;

  @Column(name = "whole_quantity")
  private Integer wholeQuantity;

  @Column(name = "half_quantity")
  private Integer halfQuantity;

  @Column(name = "quarter_quantity")
  private Integer quarterQuantity;

  @ManyToOne
  @JoinColumn(name = "id_dispatch")
  private DispatchEntity dispatch;
}
