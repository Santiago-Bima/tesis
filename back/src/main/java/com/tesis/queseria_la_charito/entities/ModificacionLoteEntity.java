package com.tesis.queseria_la_charito.entities;

import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "movimientos_lotes")
public class ModificacionLoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "cantidadPrevia")
    private Integer cantidadPrevia;

    @Column(name = "cantidadPosterior")
    private Integer cantidadPosterior;

    @Column(name = "nuevo")
    private boolean nuevo;

    @ManyToOne
    @JoinColumn(name = "id_lote")
    private LoteEntity lote;

    @ManyToOne
    @JoinColumn(name = "username")
    private UsuarioEntity usuario;
}
