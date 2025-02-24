package com.tesis.queseria_la_charito.entities.compra;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "comprobantes_compra")
public class ComprobanteCompraEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_comprobante", unique = true)
  private Long id;

  @Column(name = "fecha")
  private LocalDate fecha;

  @Column(name = "total")
  private Integer total;

  @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL)
  private List<DetalleComprobanteEntity> listaDetalles;
}
