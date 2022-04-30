package com.downvoteit.springsolacecommon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {
  private Integer id;
  private Integer categoryId;
  private String name;
  private Integer amount;
  private Double price;
}
