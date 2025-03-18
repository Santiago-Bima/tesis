package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "formulas")
public class FormulaEntity {
    @Id
    @Column(name = "id_formula", unique = true)
    private String code;

    @Column(name = "milk_quantity")
    private Integer milkQuantity;

    @ManyToOne
    @JoinColumn(name = "id_cheese_type")
    private CheeseTypeEntity cheeseType;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaDetailEntity> lstDetails;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.REFRESH)
    private List<ProductionEntity> lstProductions;
}
