package com.downvoteit.springdemos.hibernatedemo.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

/** The type Store item. */
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
