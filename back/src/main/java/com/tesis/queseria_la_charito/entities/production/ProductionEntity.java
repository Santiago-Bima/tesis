package com.tesis.queseria_la_charito.entities.production;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.production.processes.QualityControlEntity;
import com.tesis.queseria_la_charito.entities.production.processes.CutDetailEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "productions")
public class ProductionEntity {
  @Id
  @Column(name = "id_production", unique = true)
  private String id;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "milk_quantity")
  private Integer milkQuantity;

  @OneToOne
  @JoinColumn(name = "id_batch", unique = true)
  private BatchEntity batch;

  @ManyToOne
  @JoinColumn(name = "username")
  private UserEntity responsible;

  @ManyToOne
  @JoinColumn(name = "id_formula")
  private FormulaEntity formula;

  @Column(name = "sauteed_time")
  private Integer sauteedTime;

  @Column(name = "maturation_start_issued")
  private LocalDate maturationStartIssued;

  @Column(name = "maturation_exit_issued")
  private LocalDate maturationExitIssued;

  @Column(name = "packaging_date")
  private LocalDate packagingDate;

  @Column(name = "painting_date")
  private LocalDate paintingDate;

  //  Procesos

  @OneToOne(mappedBy = "production", cascade = CascadeType.ALL)
  private CutDetailEntity cutDetail;

  @OneToOne(mappedBy = "production", cascade = CascadeType.ALL)
  private QualityControlEntity qualityControl;
}
