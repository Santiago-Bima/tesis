package com.tesis.queseria_la_charito.entities;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.purchase.ProviderEntity;
import com.tesis.queseria_la_charito.entities.stockControl.InsumoControlEntity;
import com.tesis.queseria_la_charito.entities.formula.CheeseTypeEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaDetailEntity;
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

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "measurement_unit")
    private String measurementUnit;

    @OneToOne(mappedBy = "item")
    private CheeseTypeEntity cheeseType;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.REFRESH)
    private List<FormulaDetailEntity> lstDetails;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REFRESH)
    private List<BatchEntity> lstBatches;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.REFRESH)
    private List<ProviderEntity> lstProviders;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.REFRESH)
    private List<InsumoControlEntity> lstControls;
}
