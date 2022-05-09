package com.downvoteit.springsolaceconsumertwo.controller;

import com.downvoteit.springhibernatecommon.entity.secondary.ItemsCategory;
import com.downvoteit.springsolaceconsumertwo.service.ItemsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping(
    value = "/items-category",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemsCategoryController {
  private final ItemsCategoryService itemsCategoryService;

  public ItemsCategoryController(ItemsCategoryService itemsCategoryService) {
    this.itemsCategoryService = itemsCategoryService;
  }

  @GetMapping("/paged")
  public Flux<ItemsCategory> getItemsCategory(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer limit) {
    log.info("Producing getItemsCategory");

    return Flux.defer(
        () -> {
          var itemMessage = itemsCategoryService.getItemsCategory(page, limit);

          return Flux.fromIterable(itemMessage);
        });
  }
}
