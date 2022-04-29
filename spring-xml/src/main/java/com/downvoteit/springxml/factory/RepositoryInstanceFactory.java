package com.downvoteit.springxml.factory;

import com.downvoteit.springxml.repository.Repository;

public class RepositoryInstanceFactory {
  public Repository getInstance(Integer id, String name) {
    return new Repository(id, name);
  }
}
