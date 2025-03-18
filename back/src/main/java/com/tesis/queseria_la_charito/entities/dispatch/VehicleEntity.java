package com.tesis.queseria_la_charito.entities.dispatch;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "vehicles")
public class VehicleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_vehicle", unique = true)
  private Long id;

  @Column(name = "plate", unique = true)
  private String plate;

  @Column(name = "disponible")
  private Boolean disponible;

  @OneToMany(mappedBy = "vehicle", cascade = CascadeType.REFRESH)
  private List<DispatchEntity> lstDispatches;
}
