package com.downvoteit.springwebflux.service;

import dto.ItemRequestDto;
import dto.ItemResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Slf4j
@Service
public class ItemService {
  private final WebClient webClient;

  public ItemService(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<ItemRequestDto> getItem(String name) {
    log.info("Routing getItem(): {}", name);

    return webClient
        .get()
        .uri("/items/{name}", name)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(ItemRequestDto.class);
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
