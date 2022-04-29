package com.downvoteit.springhibernate.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "store_items")
public class StoreItem extends Item {
  @Column(name = "store_name")
  private String storeName;
}
