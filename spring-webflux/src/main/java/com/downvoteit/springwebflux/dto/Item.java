package com.downvoteit.springwebflux.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
  private Integer id;
  private Integer categoryId;
  private String name;
  private Integer amount;
  private Double price;
}
