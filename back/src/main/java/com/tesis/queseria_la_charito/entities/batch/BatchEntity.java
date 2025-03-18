package com.tesis.queseria_la_charito.entities.batch;

import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptDetailEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchDetailEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "batches")
public class BatchEntity {
    @Id
    @Column(name = "id_batch", unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_item")
    private ItemEntity item;

    @Column(name = "status")
    private String status;

    @Column(name = "units")
    private Integer units;

    @Column(name = "show")
    private boolean show;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.REFRESH)
    private List<BatchModificationEntity> lstModifications;

    @OneToOne(mappedBy = "batch")
    private ProductionEntity production;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.REFRESH)
    private List<DispatchDetailEntity> lstDispatchDetails;

    @OneToOne(mappedBy = "batch")
    private ReceiptDetailEntity lstReceipDetails;
}
