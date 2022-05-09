package com.downvoteit.springsolaceproducer.controller;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ResDto;
import com.downvoteit.springsolaceproducer.service.ItemService;
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

  @PostMapping
  public Mono<ResDto> createItem(@RequestBody ItemReqDto dto) {
    log.info("Producing createItem");

    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.createItem(dto);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }

  @GetMapping("/row/{categoryId}/{name}")
  public Mono<ItemReqDto> getItem(@PathVariable Integer categoryId, @PathVariable String name) {
    log.info("Producing getItem");

    var dto = ItemFilterDto.builder().categoryId(categoryId).name(name).build();

    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.getItem(dto);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }

  @GetMapping("/paged")
  public Flux<ItemReqDto> getItems(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer limit) {
    log.info("Producing getItems");

    return Flux.defer(
        () -> {
          try {
            var itemMessage = itemService.getItems(page, limit);

            return Flux.fromIterable(itemMessage.getList());
          } catch (JCSMPException e) {
            return Flux.error(e);
          }
        });
  }

  @DeleteMapping("/{id}")
  public Mono<ResDto> deleteItem(@PathVariable Integer id) {
    log.info("Producing deleteItem");

    return Mono.defer(
        () -> {
          try {
            var itemMessage = itemService.deleteItem(id);

            return Mono.just(itemMessage);
          } catch (JCSMPException e) {
            return Mono.error(e);
          }
        });
  }
}
