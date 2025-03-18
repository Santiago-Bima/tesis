package com.tesis.queseria_la_charito.entities.stockControl;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "stock_controls")
public class StockControlEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_control", unique = true)
  private Long id;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "expected_whole_quantity")
  private Integer expectedWholeQuantity;

  @Column(name = "obtained_whole_quantity")
  private Integer obtainedWholeQuantity;

  @Column(name = "expected_half_quantity")
  private Integer expectedHalfQuantity;

  @Column(name = "obtained_half_quantity")
  private Integer obtained_half_quantity;

  @Column(name = "expected_quarter_quantity")
  private Integer expectedQuarterQuantity;

  @Column(name = "obtained_quarter_quantity")
  private Integer obtainedQuarterQuantity;

  @Column(name = "isNew")
  private boolean isNew;

  @OneToMany(mappedBy = "stockControl", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InsumoControlEntity> lstExpectedSuppliesControl;

  @OneToMany(mappedBy = "controlStock", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InsumoControlEntity> lstObtainedSuppliesControl;

  @Column(name = "observations")
  private String observations;

  @ManyToOne
  @JoinColumn(name = "username")
  private UserEntity responsible;
}
