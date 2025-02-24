package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("items")
public class ItemController {
  @Autowired
  private ItemService service;

  @GetMapping()
  List<String> getNames() { return service.getNames(); }
}
