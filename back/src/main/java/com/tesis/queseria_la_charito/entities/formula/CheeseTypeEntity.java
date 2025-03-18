package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "cheese_types")
public class CheeseTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cheese_type", unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_cheese", unique = true)
    private ItemEntity item;

    @Column(name = "maturation_days")
    private Integer maturationDays;

    @OneToMany(mappedBy = "cheeseType", cascade = CascadeType.REFRESH)
    private List<FormulaEntity> lstFormulas;
}
