package com.tesis.queseria_la_charito.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
  List<String> getNames();
}
