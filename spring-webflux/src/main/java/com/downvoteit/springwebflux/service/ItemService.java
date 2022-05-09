package com.downvoteit.springwebflux.service;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ResDto;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ItemService {
  private final WebClient webClient;
  private LoadingCache<ItemFilterDto, Object> cache;

  public ItemService(WebClient webClient) {
    this.webClient = webClient;
  }

  @PostConstruct
  void launchCache() {
    log.info("Launching caffeine cache");
    cache =
        Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .evictionListener(
                (o1, o2, removalCause) -> log.info("Evicted: {}, {}, {}", o1, o2, removalCause))
            .build(this::getItem);
  }

  @PreDestroy
  void cleanCache() {
    log.info("Cleaning caffeine cache");
    cache.cleanUp();
  }

  @Scheduled(fixedDelay = 5000)
  void printStats() {
    cache.asMap().keySet().stream().map(o -> "Cache map keys: " + o).forEach(log::info);
  }

  public Mono<ResDto> createItem(ItemReqDto dto) {
    log.info("Routing createItem");

    return webClient
        .post()
        .uri("/items")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(dto))
        .retrieve()
        .bodyToMono(ResDto.class);
  }

  @SuppressWarnings("deprecation")
  public Mono<ItemReqDto> getItem(ItemFilterDto filter) {
    log.info("Routing getItem");

    //    var mono =
    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/items/row/{categoryId}/{name}")
                    .build(filter.getCategoryId(), filter.getName()))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(ItemReqDto.class);
    //    return CacheMono.lookup(cache.asMap(), name)
    //        .onCacheMissResume(
    //            () ->
    //                mono.cast(Object.class)
    //                    .doOnNext(o -> ItemReqDto.builder().id(0).name("Dummy").build()))
    //        .cast(ItemReqDto.class);
  }

  public Flux<ItemReqDto> getItems(Integer page, Integer limit) {
    log.info("Routing getItems");

    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/items/paged")
                    .queryParam("page", page)
                    .queryParam("limit", limit)
                    .build())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToFlux(ItemReqDto.class);
  }

  public Mono<ResDto> deleteItem(Integer id) {
    log.info("Routing deleteItem");

    cache.invalidateAll();

    return webClient
        .delete()
        .uri("/items/{id}", id)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(ResDto.class);
  }
}
