package com.logicgate.farm.util;

import com.logicgate.farm.domain.Color;

import java.util.concurrent.ThreadLocalRandom;

public final class FarmUtils {

  private static final String ANIMAL_NAME = "Animal-%d";

  private static final String BARN_NAME = "Barn-%d";

  private static final int COLOR_SIZE = Color.values().length;

  private static final int BARN_CAPACITY = 20;


  public static String animalName(int value) {
    return String.format(ANIMAL_NAME, value);
  }

  public static String barnName(int value) {
    return String.format(BARN_NAME, value);
  }

  public static Color randomColor() {
    return Color.values()[ThreadLocalRandom.current().nextInt(0, COLOR_SIZE)];
  }

  public static Integer barnCapacity() {
    return BARN_CAPACITY;
  }

  private FarmUtils() {
  }

}
