package com.logicgate.farm.exception;

/**
 * This exception shouldn't be thrown, but, to be safe, it's being added in case someone else updates
 * my code down the line and changes the expected format of the code.
 */
public class BadDataException extends RuntimeException {
  public BadDataException(String message) {
    super(message);
  }
}
