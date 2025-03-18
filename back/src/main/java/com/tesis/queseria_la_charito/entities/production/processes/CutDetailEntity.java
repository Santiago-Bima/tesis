package com.tesis.queseria_la_charito.entities.production.processes;

import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cuts_details")
public class CutDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_production", unique = true)
    private ProductionEntity production;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "cut")
    private String cut;
}
