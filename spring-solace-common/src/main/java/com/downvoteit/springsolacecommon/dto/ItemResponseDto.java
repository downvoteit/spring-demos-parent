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
public class ItemResponseDto {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("message")
  private String message;
}
