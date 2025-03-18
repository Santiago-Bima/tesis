package com.tesis.queseria_la_charito.entities.production.processes;

import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "quality_controls")
public class QualityControlEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "taste_test")
  private String tasteTest;

  @Column(name = "consistency_taste")
  private String consistencyTaste;

  @Column(name = "smell_taste")
  private String smellTaste;

  @Column(name = "observations")
  private String observations;

  @OneToOne
  @JoinColumn(name = "id_production", unique = true)
  private ProductionEntity production;
}
