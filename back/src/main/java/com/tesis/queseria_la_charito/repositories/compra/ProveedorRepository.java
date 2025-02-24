package com.tesis.queseria_la_charito.repositories.compra;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.compra.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
  List<ProveedorEntity> findByInsumoAndMostrar(ItemEntity item, boolean mostrar);
  Optional<ProveedorEntity> findByIdAndMostrar(Long id, boolean mostrar);
}
