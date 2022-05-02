package com.downvoteit.springwebflux.controller;

import com.downvoteit.springwebflux.service.ItemService;
import dto.ItemRequestDto;
import dto.ItemResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

  @PostMapping
  public Mono<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequestDto) {
    return itemService.createItem(itemRequestDto);
  }

  @GetMapping("/{name}")
  public Mono<ItemRequestDto> getItem(@PathVariable String name) {
    return itemService.getItem(name);
  }

  @GetMapping
  public Flux<ItemRequestDto> getItems() {
    return Flux.fromStream(itemService.getItems());
  }
}
