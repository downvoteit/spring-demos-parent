package com.downvoteit.springwebflux.service;

import com.downvoteit.springwebflux.dto.Item;
import com.downvoteit.springwebflux.dto.ItemMessage;
import com.solacesystems.jcsmp.JCSMPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.Stream;

@Slf4j
@Service
public class ItemService {
  private final ProducerService producerService;
  private SecureRandom random;

  public ItemService(ProducerService producerService) {
    this.producerService = producerService;
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      log.error("", e);
    }
  }

  public Item getItem() {
    return createRandomItem(null);
  }

  public Stream<Item> getItems() {
    return Stream.iterate(1, o -> o <= 10, o -> o + 1).map(this::createRandomItem);
  }

  private Item createRandomItem(Integer id) {
    return Item.builder()
        .id(id != null ? id : random.nextInt(10))
        .categoryId(random.nextInt(100))
        .name("New item")
        .amount(random.nextInt(10000))
        .price(random.nextInt(10) * 10_000 * random.nextDouble())
        .build();
  }

  public ItemMessage createItem(Item item) throws JCSMPException {
    return ItemMessage.builder().id(producerService.send(item)).message("Created").build();
  }
}
