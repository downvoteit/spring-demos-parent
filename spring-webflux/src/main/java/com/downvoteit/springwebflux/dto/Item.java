package com.downvoteit.springwebflux.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
  private Integer id;
  private Integer categoryId;
  private String name;
  private Integer amount;
  private Double price;
}