package com.downvoteit.springsolacecommon.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckedPersistenceException extends Exception {
  public CheckedPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
