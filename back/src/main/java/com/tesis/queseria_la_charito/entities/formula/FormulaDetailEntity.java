package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "formulas_details")
public class FormulaDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_formula")
    private FormulaEntity formula;

    @ManyToOne
    @JoinColumn(name = "id_item")
    private ItemEntity supply;

    @Column(name = "quantity")
    private Integer quantity;
}
