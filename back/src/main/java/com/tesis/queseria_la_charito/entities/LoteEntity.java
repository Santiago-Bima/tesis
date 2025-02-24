package com.tesis.queseria_la_charito.entities;

import com.tesis.queseria_la_charito.entities.compra.DetalleComprobanteEntity;
import com.tesis.queseria_la_charito.entities.despacho.DetalleDespachoEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "lotes")
public class LoteEntity {
    @Id
    @Column(name = "id_lote", unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_item")
    private ItemEntity item;

    @Column(name = "estado")
    private String estado;

    @Column(name = "unidades")
    private Integer unidades;

    @Column(name = "mostrar")
    private boolean mostrar;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.REFRESH)
    private List<ModificacionLoteEntity> listaModificaciones;

    @OneToOne(mappedBy = "lote")
    private ElaboracionEntity elaboracion;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.REFRESH)
    private List<DetalleDespachoEntity> lstDetallesDespachos;

    @OneToOne(mappedBy = "lote")
    private DetalleComprobanteEntity detalleComprobante;
}
