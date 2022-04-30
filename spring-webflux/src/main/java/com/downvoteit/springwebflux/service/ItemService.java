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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.Stream;

@Slf4j
@Service
public class ItemService {
  private final WebClient webClient;
  private SecureRandom random;

  public ItemService(WebClient webClient) {
    this.webClient = webClient;
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      log.error("", e);
    }
  }

  public ItemRequestDto getItem() {
    return createRandomItem(null);
  }

  public Stream<ItemRequestDto> getItems() {
    return Stream.iterate(1, o -> o <= 10, o -> o + 1).map(this::createRandomItem);
  }

  private ItemRequestDto createRandomItem(Integer id) {
    return ItemRequestDto.builder()
        .id(id != null ? id : random.nextInt(10))
        .categoryId(random.nextInt(100))
        .name("New item")
        .amount(random.nextInt(10000))
        .price(random.nextInt(10) * 10_000 * random.nextDouble())
        .build();
  }

  public Mono<ItemResponseDto> createItem(String mode, ItemRequestDto itemRequestDto) {
    return webClient
        .post()
        .uri("/items/{mode}", mode)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(itemRequestDto))
        .retrieve()
        .bodyToMono(ItemResponseDto.class);
  }
}
