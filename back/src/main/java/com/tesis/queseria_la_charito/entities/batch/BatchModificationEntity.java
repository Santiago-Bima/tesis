package com.tesis.queseria_la_charito.entities.batch;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "batch_movements")
public class BatchModificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "previousQuantity")
    private Integer previousQuantity;

    @Column(name = "subsequentQuantity")
    private Integer subsequentQuantity;

    @Column(name = "isNew")
    private boolean isNew;

    @ManyToOne
    @JoinColumn(name = "id_batch")
    private BatchEntity batch;

    @ManyToOne
    @JoinColumn(name = "username")
    private UserEntity responsible;
}
