package com.downvoteit.springwebflux.service;

import com.downvoteit.springcommon.dto.ItemReqDto;
import com.downvoteit.springcommon.dto.ItemResDto;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ItemService {
  private final WebClient webClient;

  public ItemService(WebClient webClient) {
    this.webClient = webClient;
  }

  @CachePut(value = "caffeine-item-cache", key = "#dto.name")
  public Mono<ItemResDto> createItem(ItemReqDto dto) {
    log.info("Routing createItem(): {}", dto);

    return webClient
        .post()
        .uri("/items")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(dto))
        .retrieve()
        .bodyToMono(ItemResDto.class);
  }

  @SuppressWarnings("deprecation")
  @Cacheable("caffeine-item-cache")
  public Mono<ItemReqDto> getItem(String name) {
    log.info("Routing getItem(): {}", name);

    var mono =
        webClient
            .get()
            .uri("/items/row/{name}", name)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(ItemReqDto.class);

    LoadingCache<String, Object> cache =
        Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .weakKeys()
            .build(this::getItem);

    return CacheMono.lookup(cache.asMap(), name)
        .onCacheMissResume(() -> mono.cast(Object.class))
        .cast(ItemReqDto.class);
  }

  public Flux<ItemReqDto> getItems(Integer page, Integer limit) {
    log.info("Routing getItems()");

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
}
