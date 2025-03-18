package com.tesis.queseria_la_charito.entities.purchase;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "receipt_details")
public class ReceiptDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_receipt_detail", unique = true)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_receipt")
  private ReceiptEntity receipt;

  @OneToOne
  @JoinColumn(name = "id_batch", unique = true)
  private BatchEntity batch;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "subtotal")
  private Integer subtotal;

  @ManyToOne
  @JoinColumn(name = "id_provider")
  private ProviderEntity provider;
}
