package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Item;
import com.laptop.ltn.laptop_store_server.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemService {
    ItemRepository itemRepository;
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getALlItems() {
        System.out.println("___________"+itemRepository);
        return itemRepository.findAll();
    }
}
