package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.ItemRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InsumoService {
    List<ItemResponse> getItems();
    ItemResponse getItemById(Long id);
    ItemResponse postItem(ItemRequest itemRequest) throws Exception;
    ItemResponse deleteItem(Long id);
    ItemResponse putItem(ItemRequest data, Long id);
}
