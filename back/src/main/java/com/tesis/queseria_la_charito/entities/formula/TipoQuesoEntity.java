package com.tesis.queseria_la_charito.entities.formula;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "tipos_quesos")
public class TipoQuesoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_queso", unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_queso", unique = true)
    private ItemEntity item;

    @Column(name = "dias_maduracion")
    private Integer diasMaduracion;

    @OneToMany(mappedBy = "tipoQueso", cascade = CascadeType.REFRESH)
    private List<FormulaEntity> listaFormulas;
}
