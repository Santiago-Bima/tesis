package com.tesis.queseria_la_charito.entities.compra;

import com.tesis.queseria_la_charito.entities.LoteEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalles_comprobante")
public class DetalleComprobanteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detallle_comprobante", unique = true)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_comprobante")
  private ComprobanteCompraEntity comprobante;

  @OneToOne
  @JoinColumn(name = "id_lote", unique = true)
  private LoteEntity lote;

  @Column(name = "cantidad")
  private Integer cantidad;

  @Column(name = "subtotal")
  private Integer subtotal;

  @ManyToOne
  @JoinColumn(name = "id_proveedor")
  private ProveedorEntity proveedor;
}
