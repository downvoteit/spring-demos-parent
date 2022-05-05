package com.downvoteit.springcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemReqDto {
  private Integer id;
  private Integer categoryId;
  private String name;
  private Integer amount;
  private Double price;
}
