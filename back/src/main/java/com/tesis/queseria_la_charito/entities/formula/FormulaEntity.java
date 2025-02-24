package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.ElaboracionEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "formulas")
public class FormulaEntity {
    @Id
    @Column(name = "id_formula", unique = true)
    private String codigo;

    @Column(name = "cantidad_leche")
    private Integer cantidadLeche;

    @ManyToOne
    @JoinColumn(name = "id_tipo_queso")
    private TipoQuesoEntity tipoQueso;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFormulaEntity> detallesFormulas;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.REFRESH)
    private List<ElaboracionEntity> listaElaboraciones;
}
