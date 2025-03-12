package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
  @Autowired
  private ItemRepository repository;


  public List<String> getNames() {
    List<String> resposes = new ArrayList<>();

    List<ItemEntity> itemEntities = repository.findAll();
    if (itemEntities.isEmpty()) {
      return new ArrayList<>();
    }

    for (ItemEntity entity : itemEntities) {
      resposes.add(entity.getNombre());
    }

    return resposes;
  }
}
