package com.downvoteit.springsolaceproducer.controller;

import com.downvoteit.springsolaceproducer.service.ItemService;
import com.solacesystems.jcsmp.JCSMPException;
import com.downvoteit.springcommon.dto.ItemRequestDto;
import com.downvoteit.springcommon.dto.ItemResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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

  @PostMapping
  public Mono<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequestDto) {
    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.createItem(itemRequestDto);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }

  @GetMapping("/{name}")
  public Mono<ItemRequestDto> getItem(@PathVariable String name) {
    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.getItem(name);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }
}
