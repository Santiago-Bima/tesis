package com.tesis.queseria_la_charito.entities.purchase;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "receipt")
public class ReceiptEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_receipt", unique = true)
  private Long id;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "total")
  private Integer total;

  @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
  private List<ReceiptDetailEntity> lstDetails;
}
