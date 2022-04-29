package com.downvoteit.springwebflux.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemMessage {
  private Integer id;
  private String message;
}
