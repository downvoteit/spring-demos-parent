package com.downvoteit.springhibernatecommon.entity.secondary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "items_categories")
@Table(name = "items_categories")
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemsCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_categories_id_seq")
  @GenericGenerator(
      name = "items_categories_id_seq",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator")
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "price")
  private Double price;
}
