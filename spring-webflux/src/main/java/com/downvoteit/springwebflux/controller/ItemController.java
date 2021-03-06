package com.downvoteit.springwebflux.controller;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ResDto;
import com.downvoteit.springwebflux.service.ItemService;
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
  public Mono<ResDto> createItem(@RequestBody ItemReqDto dto) {
    return itemService.createItem(dto);
  }

  @GetMapping("/row/{categoryId}/{name}")
  public Mono<ItemReqDto> getItem(@PathVariable Integer categoryId, @PathVariable String name) {
    var dto = ItemFilterDto.builder().categoryId(categoryId).name(name).build();

    return itemService.getItem(dto);
  }

  @GetMapping("/paged")
  public Flux<ItemReqDto> getItems(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer limit) {
    return itemService.getItems(page, limit);
  }

  @DeleteMapping("/{id}")
  public Mono<ResDto> deleteItem(@PathVariable Integer id) {
    return itemService.deleteItem(id);
  }
}
