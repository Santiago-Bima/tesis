package com.tesis.queseria_la_charito.entities;

import com.tesis.queseria_la_charito.entities.compra.ProveedorEntity;
import com.tesis.queseria_la_charito.entities.controlStock.InsumoControlEntity;
import com.tesis.queseria_la_charito.entities.formula.DetalleFormulaEntity;
import com.tesis.queseria_la_charito.entities.formula.TipoQuesoEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "items")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item", unique = true)
    private Long id;

    @Column(name = "nombre", unique = true)
    private String nombre;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @OneToOne(mappedBy = "item")
    private TipoQuesoEntity tipoQueso;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.REFRESH)
    private List<DetalleFormulaEntity> listaDetalles;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REFRESH)
    private List<LoteEntity> listaLotes;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.REFRESH)
    private List<ProveedorEntity> listaProveedores;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.REFRESH)
    private List<InsumoControlEntity> listaControlesInsumo;
}
