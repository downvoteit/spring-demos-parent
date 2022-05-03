package com.downvoteit.springwebflux.service;

import com.downvoteit.springcommon.dto.ItemRequestDto;
import com.downvoteit.springcommon.dto.ItemResponseDto;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
public class ItemService {
  private final WebClient webClient;
  private final LoadingCache<String, Object> cache;

  public ItemService(WebClient webClient) {
    this.webClient = webClient;
    this.cache =
        Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .refreshAfterWrite(2, TimeUnit.MINUTES)
            .weakKeys()
            .build(this::getItem);
  }

  @SuppressWarnings("deprecation")
  @Cacheable("caffeine-item-cache")
  public Mono<ItemRequestDto> getItem(String name) {
    log.info("Routing getItem(): {}", name);

    var mono =
        webClient
            .get()
            .uri("/items/{name}", name)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(ItemRequestDto.class);

    return CacheMono.lookup(cache.asMap(), name)
        .onCacheMissResume(() -> mono.cast(Object.class))
        .cast(ItemRequestDto.class);
  }

  public Stream<ItemRequestDto> getItems() {
    return Stream.empty();
  }

  public Mono<ItemResponseDto> createItem(ItemRequestDto itemRequestDto) {
    log.info("Routing createItem(): {}", itemRequestDto);

    return webClient
        .post()
        .uri("/items")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(itemRequestDto))
        .retrieve()
        .bodyToMono(ItemResponseDto.class);
  }
}
