package com.logicgate.farm;

import com.logicgate.farm.domain.Animal;
import com.logicgate.farm.domain.Barn;
import com.logicgate.farm.domain.Color;
import com.logicgate.farm.exception.AnimalNotFoundException;
import com.logicgate.farm.repository.BarnRepository;
import com.logicgate.farm.service.AnimalService;
import com.logicgate.farm.util.FarmUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {

  private static final int ANIMAL_SEED = 1000;

  @Autowired
  private BarnRepository barnRepository;

  @Autowired
  private AnimalService animalService;

  @After
  public void tearDown() {
    animalService.deleteAll();
    barnRepository.deleteAll();
  }

  @Test
  public void addOneAnimal() {
    animalService.addToFarm(new Animal(FarmUtils.animalName(0), Color.DARKER_THAN_BLACK));

    checkAnimals(1);
  }

  @Test
  public void deleteOneAnimal() {
    Animal added = animalService.addToFarm(new Animal(FarmUtils.animalName(0), Color.DARKER_THAN_BLACK));

    checkAnimals(1);

    animalService.removeFromFarm(added);

    checkAnimals(0);
  }

  @Test
  public void addAnimalsToFarm_CapacityPlus1() {
    animalService.addToFarm(IntStream.range(0, FarmUtils.barnCapacity() + 1)
        .mapToObj(value -> new Animal(FarmUtils.animalName(value), Color.DARKER_THAN_BLACK))
        .collect(Collectors.toList()));

    checkAnimals(FarmUtils.barnCapacity() + 1);
  }

  @Test
  public void removeAllAnimalsFromFarm_CapacityPlus1() {
    animalService.addToFarm(IntStream.range(0, FarmUtils.barnCapacity() + 1)
        .mapToObj(value -> new Animal(FarmUtils.animalName(value), Color.DARKER_THAN_BLACK))
        .collect(Collectors.toList()));

    checkAnimals(FarmUtils.barnCapacity() + 1);

    List<Animal> allAnimals = animalService.findAll();

    animalService.removeFromFarm(allAnimals);

    checkAnimals(0);
  }

  /*
  I wrote this test case since my code originally was failing when removing from an almost full barnyard.
  20, 20, 19, 19, 20 -> originally, if the 19 was picked, it would just remove it and not rebalance correctly.
  That's why the test is still here.
   */
  @Test
  public void removeAnimalsFromFarm_edgeCaseWhereTheNumberAreCloseToCapacity() {
    int capacity = FarmUtils.barnCapacity() * 5 - 2;

    animalService.addToFarm(IntStream.range(0, capacity)
        .mapToObj(value -> new Animal(FarmUtils.animalName(value), Color.DARKER_THAN_BLACK))
        .collect(Collectors.toList()));

    checkAnimals(capacity);

    List<Animal> animalResult = animalService.findAll();

    Map<Barn, List<Animal>> barnAnimalMap = animalResult.stream()
        .collect(Collectors.groupingBy(Animal::getBarn));

    List<Map.Entry<Barn, List<Animal>>> list = barnAnimalMap.entrySet().stream()
        .filter(entry -> entry.getValue().size() == FarmUtils.barnCapacity() - 1)
        .collect(Collectors.toList());

    animalService.removeFromFarm(list.get(0).getValue().get(0));

    checkAnimals(capacity - 1);
  }

  @Test
  public void addAnimalsToFarm() {
    animalService.addToFarm(IntStream.range(0, ANIMAL_SEED)
        .mapToObj(value -> new Animal(FarmUtils.animalName(value), FarmUtils.randomColor()))
        .collect(Collectors.toList()));

    checkAnimals(ANIMAL_SEED);
  }

  @Test
  public void removeAnimalsFromFarm() {
    animalService.addToFarm(IntStream.range(0, ANIMAL_SEED)
        .mapToObj(value -> new Animal(FarmUtils.animalName(value), FarmUtils.randomColor()))
        .collect(Collectors.toList()));

    List<Animal> animals = animalService.findAll();
    List<Animal> animalsToRemove = animals.stream()
        .filter(animal -> ThreadLocalRandom.current().nextBoolean())
        .collect(Collectors.toList());

    animalService.removeFromFarm(animalsToRemove);

    checkAnimals(animals.size() - animalsToRemove.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void animalAlreadyAdded() {
    Animal added = animalService.addToFarm(new Animal(FarmUtils.animalName(0), Color.DARKER_THAN_BLACK));

    animalService.addToFarm(added);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addAnimalInBarn() {
    animalService.addToFarm(new Animal("animal", Color.DARKER_THAN_BLACK, new Barn("barn", Color.DARKER_THAN_BLACK)));
  }

  @Test(expected = NullPointerException.class)
  public void removeAnimalNotInBarn() {
    animalService.removeFromFarm(new Animal("animal", Color.DARKER_THAN_BLACK));
  }

  @Test(expected = AnimalNotFoundException.class)
  public void removeAnimalWithId() {
    Animal added = animalService.addToFarm(new Animal(FarmUtils.animalName(0), Color.DARKER_THAN_BLACK));

    animalService.removeFromFarm(added);
    animalService.removeFromFarm(added);
  }

  private void checkAnimals(int expected) {
    List<Animal> animalResult = animalService.findAll();
    assertThat("Animal updates should reflect in persisted entities.", animalResult.size(), is(expected));

    Map<Barn, List<Animal>> barnAnimalMap = animalResult.stream()
        .collect(Collectors.groupingBy(Animal::getBarn));

    barnAnimalMap.forEach((barn, animals) -> {
      assertThat("Barns should not exceed capacity.", barn.getCapacity(), greaterThanOrEqualTo(animals.size()));
      assertThat("Animals should match the barn color.",
          animals.stream().anyMatch(animal -> animal.getFavoriteColor() != barn.getColor()), is(false));
    });

    // no unused barns
    assertThat("No barns should be empty.", barnRepository.count(), is((long) barnAnimalMap.keySet().size()));

    Map<Color, List<Barn>> colorBarnMap = barnAnimalMap.keySet().stream()
        .collect(Collectors.groupingBy(Barn::getColor));

    colorBarnMap.forEach((color, barns) -> {
      Integer minCapacity = barns.stream()
          .mapToInt(Barn::getCapacity).min()
          .orElse(FarmUtils.barnCapacity());

      List<Integer> unusedCapacity = barns.stream()
          .map(barn -> barn.getCapacity() - barnAnimalMap.get(barn).size())
          .collect(Collectors.toList());

      Integer totalUnusedCapacity = unusedCapacity.stream()
          .mapToInt(i -> i)
          .sum();

      assertThat("Optimal barns should exist for capacity requirements.",
          minCapacity, greaterThan(totalUnusedCapacity));
      assertThat("Animal distribution should maximize free barn space.",
          Collections.max(unusedCapacity) - Collections.min(unusedCapacity), lessThanOrEqualTo(1));
    });
  }
}
