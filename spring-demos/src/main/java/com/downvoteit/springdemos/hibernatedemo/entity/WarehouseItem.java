package com.downvoteit.springdemos.hibernatedemo.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "warehouse_items")
public class WarehouseItem extends Item {
  @Column(name = "warehouse_name")
  private String warehouseName;
}
