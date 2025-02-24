package com.tesis.queseria_la_charito.entities.procesosElaboracion;

import com.tesis.queseria_la_charito.entities.ElaboracionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "controles_calidad")
public class ControlCalidadEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha")
  private LocalDate fecha;

  @Column(name = "prueba_sabor")
  private String pruebaSabor;

  @Column(name = "prueba_concistencia")
  private String pruebaConcistencia;

  @Column(name = "prueba_aroma")
  private String pruebaAroma;

  @Column(name = "observacion")
  private String observacion;

  @OneToOne
  @JoinColumn(name = "id_elaboracion", unique = true)
  private ElaboracionEntity elaboracion;
}
