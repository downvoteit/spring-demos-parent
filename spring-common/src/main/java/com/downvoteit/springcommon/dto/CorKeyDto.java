package com.downvoteit.springcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorKeyDto {
  private Integer id;
  private volatile boolean acked;
  private volatile boolean published;
}
