package com.tesis.queseria_la_charito.entities;

import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.procesosElaboracion.*;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "elaboraciones")
public class ElaboracionEntity {
  @Id
  @Column(name = "id_elaboracion", unique = true)
  private String id;

  @Column(name = "fecha")
  private LocalDate fecha;

  @Column(name = "cantidad_leche")
  private Integer cantidadLeche;

  @OneToOne
  @JoinColumn(name = "id_lote", unique = true)
  private LoteEntity lote;

  @ManyToOne
  @JoinColumn(name = "username")
  private UsuarioEntity usuario;

  @ManyToOne
  @JoinColumn(name = "id_formula")
  private FormulaEntity formula;

  @Column(name = "tiempo_salado")
  private Integer tiempoSalado;

  @Column(name = "fecha_entrada_maduracion")
  private LocalDate fechaEntradaMaduracion;

  @Column(name = "fecha_salida_maduracion")
  private LocalDate fechaSalidaMaduracion;

  @Column(name = "fecha_embolsado")
  private LocalDate fechaEmbolsado;

  @Column(name = "fecha_pintado")
  private LocalDate fechaPintado;

  //  Procesos

  @OneToOne(mappedBy = "elaboracion", cascade = CascadeType.ALL)
  private DetalleCorteEntity detalleCorte;

  @OneToOne(mappedBy = "elaboracion", cascade = CascadeType.ALL)
  private ControlCalidadEntity controlCalidad;
}
