package com.logicgate.farm.exception;

public class AnimalNotFoundException extends RuntimeException {
  public AnimalNotFoundException(String message) {
    super(message);
  }
}
