package com.downvoteit.springwebflux.controller;

import com.downvoteit.springwebflux.service.ItemService;
import dto.ItemRequestDto;
import dto.ItemResponseDto;
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
  public Mono<ItemRequestDto> getItem() {
    return Mono.fromCallable(itemService::getItem);
  }

  @GetMapping("/all")
  public Flux<ItemRequestDto> getItems() {
    return Flux.fromStream(itemService.getItems());
  }

  @PostMapping("/{mode}")
  public Mono<ItemResponseDto> createItem(
      @PathVariable String mode, @RequestBody ItemRequestDto itemRequestDto) {
    return itemService.createItem(mode, itemRequestDto);
  }
}
