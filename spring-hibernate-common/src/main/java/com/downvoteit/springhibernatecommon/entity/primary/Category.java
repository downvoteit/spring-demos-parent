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
@Entity(name = "categories")
@Table(name = "categories")
@Inheritance(strategy = InheritanceType.JOINED)
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq")
  @GenericGenerator(
      name = "categories_id_seq",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator")
  private Integer id;

  @Column(name = "name")
  private String name;
}
