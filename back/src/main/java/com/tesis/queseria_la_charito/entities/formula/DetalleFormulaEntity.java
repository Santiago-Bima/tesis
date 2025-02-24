package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_formulas")
public class DetalleFormulaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_formula")
    private FormulaEntity formula;

    @ManyToOne
    @JoinColumn(name = "id_item")
    private ItemEntity insumo;

    @Column(name = "cantidad")
    private Integer cantidad;
}
