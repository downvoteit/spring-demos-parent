package com.downvoteit.springxml.factory;

import com.downvoteit.springxml.repository.Repository;

public class RepositoryStaticFactory {
  private RepositoryStaticFactory() {}

  public static Repository getInstance(Integer id, String name) {
    return new Repository(id, name);
  }
}
