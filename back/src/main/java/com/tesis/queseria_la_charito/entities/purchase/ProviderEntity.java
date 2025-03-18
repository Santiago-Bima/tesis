package com.tesis.queseria_la_charito.entities.purchase;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "providers")
public class ProviderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_provider", unique = true)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "email")
  private String email;

  @Column(name = "alias")
  private String alias;

  @Column(name = "CUIT")
  private String cuit;

  @Column(name = "bank")
  private String bank;

  @Column(name = "account_type")
  private String accountType;

  @Column(name = "phone")
  private Long phone;

  @ManyToOne
  @JoinColumn(name = "id_item")
  private ItemEntity supply;

  @Column(name = "measured_quantity")
  private Integer measuredQuantity;

  @Column(name = "measurement_unit")
  private String measurement_unit;

  @Column(name = "cost")
  private Integer cost;

  @Column(name = "show")
  private boolean show;

  @OneToMany(mappedBy = "provider", cascade = CascadeType.REFRESH)
  private List<ReceiptDetailEntity> lstReceiptDetails;
}
