package com.tesis.queseria_la_charito.entities.dispatch;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "dispatches")
public class DispatchEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_dispatch", unique = true)
  private Long id;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "cheese")
  private String cheese;

  @Column(name = "total_quantity")
  private Integer totalQuantity;

  @Column(name = "status")
  private String status;

  @ManyToOne
  @JoinColumn(name = "id_destination")
  private DestinationEntity destination;

  @ManyToOne
  @JoinColumn(name = "id_vehicle")
  private VehicleEntity vehicle;

  @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL)
  private List<DispatchDetailEntity> lstDetails;

  @ManyToOne
  @JoinColumn(name = "responsible")
  private UserEntity responsible;
}
