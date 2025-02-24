package com.tesis.queseria_la_charito.entities.procesosElaboracion;

import com.tesis.queseria_la_charito.entities.ElaboracionEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalles_corte")
public class DetalleCorteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_elaboracion", unique = true)
    private ElaboracionEntity elaboracion;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "corte")
    private String corte;
}
