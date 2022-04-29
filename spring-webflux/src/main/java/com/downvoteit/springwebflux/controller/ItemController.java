package com.downvoteit.springwebflux.controller;

import com.downvoteit.springwebflux.dto.Item;
import com.downvoteit.springwebflux.dto.ItemMessage;
import com.downvoteit.springwebflux.service.ItemService;
import com.solacesystems.jcsmp.JCSMPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(
    value = "/items",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {
  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  public Mono<Item> getItem() {
    return Mono.fromCallable(itemService::getItem);
  }

  @GetMapping("/all")
  public Flux<Item> getItems() {
    return Flux.fromStream(itemService.getItems());
  }

  @PostMapping
  public Mono<ItemMessage> createItem(@RequestBody Item item) {
    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.createItem(item);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }
}
