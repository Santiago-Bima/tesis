package com.tesis.queseria_la_charito.entities.compra;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Entity
@Data
@Table(name = "proveedores")
public class ProveedorEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_proveedor", unique = true)
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "email")
  private String email;

  @Column(name = "alias")
  private String alias;

  @Column(name = "CUIT")
  private String cuit;

  @Column(name = "banco")
  private String banco;

  @Column(name = "tipo_cuenta")
  private String tipoCuenta;

  @Column(name = "telefono")
  private Long telefono;

  @ManyToOne
  @JoinColumn(name = "id_item")
  private ItemEntity insumo;

  @Column(name = "cantidad_medida")
  private Integer cantidadMedida;

  @Column(name = "unidad_medida")
  private String unidadMedida;

  @Column(name = "costo")
  private Integer costo;

  @Column(name = "mostrar")
  private boolean mostrar;

  @OneToMany(mappedBy = "proveedor", cascade = CascadeType.REFRESH)
  private List<DetalleComprobanteEntity> listaDetallesComprobante;
}
