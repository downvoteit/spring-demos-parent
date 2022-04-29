package com.downvoteit.springxml.service;

import com.downvoteit.springxml.repository.Repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListService {
  private List<Repository> repositories;
}
