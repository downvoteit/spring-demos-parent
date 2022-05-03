package com.downvoteit.springhibernatecommon.entity.primary;

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
@Entity(name = "items")
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_id_seq")
  @GenericGenerator(
      name = "items_id_seq",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator")
  private Integer id;

  @OneToOne
  private Category category;

  @Column(name = "name")
  private String name;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "price")
  private Double price;
}
