package com.downvoteit.springsolacecommon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDto {
  private Integer id;
  private String message;
}
