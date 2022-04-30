package com.downvoteit.springsolacecommon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("categoryId")
  private Integer categoryId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("price")
  private Double price;
}
