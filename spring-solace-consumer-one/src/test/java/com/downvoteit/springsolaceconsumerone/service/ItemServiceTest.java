package com.downvoteit.springsolaceconsumerone.service;

import com.downvoteit.springcommon.dto.ItemFilterDto;
import com.downvoteit.springproto.CategoryProto;
import com.downvoteit.springproto.ItemReqProto;
import com.downvoteit.springsolaceconsumerone.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemServiceTest {
  private Cache cache;

  @MockBean private ItemRepository mockItemRepository;
  @Autowired private String itemCacheName;
  @Autowired private ItemService itemService;
  @Autowired private CacheManager cacheManager;

  @BeforeEach
  void setCache() {
    cache = cacheManager.getCache(itemCacheName);

    if (cache == null) Assertions.fail();

    cache.clear();
  }

  @AfterEach
  void clearCache() {
    cache.clear();
  }

  @Test
  void getItem_mustAssertVerifyCacheHit_PositiveTest() {
    var dto = ItemFilterDto.builder().categoryId(1).name("test").build();

    var item =
        ItemReqProto.newBuilder()
            .setId(1)
            .setCategoryId(CategoryProto.forNumber(dto.getCategoryId()))
            .setName(dto.getName())
            .setAmount(1)
            .setPrice(1D)
            .build();

    given(mockItemRepository.getItem(dto)).willReturn(item);

    var itemCacheMiss = itemService.getItem(dto);
    var itemCacheHit = itemService.getItem(dto);

    assertThat(itemCacheMiss).isEqualTo(item);
    assertThat(itemCacheHit).isEqualTo(item);

    verify(mockItemRepository, times(1)).getItem(dto);
    assertThat(getItemCache(dto)).isEqualTo(item);
  }

  private ItemReqProto getItemCache(ItemFilterDto dto) {
    Cache.ValueWrapper wrapper = cache.get(dto);

    if (wrapper == null) return null;

    return (ItemReqProto) wrapper.get();
  }
}
