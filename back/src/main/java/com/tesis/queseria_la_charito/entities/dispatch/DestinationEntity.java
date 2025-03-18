package com.tesis.queseria_la_charito.entities.dispatch;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "destinations")
public class DestinationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_destination", unique = true)
  private Long id;

  @Column(name = "street")
  private String street;

  @Column(name = "number")
  private Integer number;

  @Column(name = "neighborhood")
  private String neighborhood;

  @OneToMany(mappedBy = "destination", cascade = CascadeType.REFRESH)
  List<DispatchEntity> lstDispatches;
}
