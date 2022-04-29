package com.downvoteit.springxml.service;

import com.downvoteit.springxml.repository.Repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {
  private Repository repositoryOne;
  private Repository repositoryTwo;
  private Repository repositoryThree;
  private Repository repositoryFour;
}
