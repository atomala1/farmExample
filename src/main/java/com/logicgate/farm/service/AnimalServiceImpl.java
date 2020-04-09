package com.logicgate.farm.service;

import com.google.common.base.Preconditions;
import com.logicgate.farm.domain.Animal;
import com.logicgate.farm.domain.Barn;
import com.logicgate.farm.domain.Color;
import com.logicgate.farm.exception.AnimalNotFoundException;
import com.logicgate.farm.exception.BadDataException;
import com.logicgate.farm.repository.AnimalRepository;
import com.logicgate.farm.repository.BarnRepository;
import com.logicgate.farm.util.FarmUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AnimalServiceImpl implements AnimalService {

  private static final Comparator<? super Map.Entry<Barn, List<Animal>>> BARN_COMPARATOR = Comparator.comparingInt(entry -> entry.getValue().size());

  private final AnimalRepository animalRepository;

  private final BarnRepository barnRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Animal> findAll() {
    return animalRepository.findAll();
  }

  @Override
  public void deleteAll() {
    animalRepository.deleteAll();
  }

  @Override
  public Animal addToFarm(Animal animal) {
    Preconditions.checkArgument(animal.getId() == null, "The animal must not have an ID.");
    Preconditions.checkArgument(animal.getBarn() == null, "The animal must not be in a barn already.");

    Map<Barn, List<Animal>> barnToAnimalsMap = getBarnToAnimalsMap(animal.getFavoriteColor());
    Optional<Map.Entry<Barn, List<Animal>>> leastPopulatedBarn = getLeastPopulatedElement(barnToAnimalsMap);

    if (!leastPopulatedBarn.isPresent() || leastPopulatedBarn.get().getValue().size() >= leastPopulatedBarn.get().getKey().getCapacity()) {
      // A new barn needs to be created
      Barn barn = barnRepository.save(new Barn(FarmUtils.barnName(barnToAnimalsMap.size()), animal.getFavoriteColor()));

      List<Animal> newAnimalList = new ArrayList<>();
      newAnimalList.add(animal);
      animal.setBarn(barn);
      animalRepository.save(animal);
      barnToAnimalsMap.put(barn, newAnimalList);

      rebalanceAnimals(barnToAnimalsMap);
    } else {
      // Just add the animal to the least populated barn.
      animal.setBarn(leastPopulatedBarn.get().getKey());
      animalRepository.save(animal);
    }

    return animal;
  }

  @Override
  public void addToFarm(List<Animal> animals) {
    animals.forEach(this::addToFarm);
  }

  @Override
  public void removeFromFarm(Animal animal) {
    Preconditions.checkNotNull(animal.getId(), "The animal must have an ID.");
    Preconditions.checkNotNull(animal.getBarn(), "The animal must be in a barn.");

    // Make sure the user passed us an existing animal.
    Animal foundAnimal = animalRepository.findById(animal.getId())
        .orElseThrow(() -> new AnimalNotFoundException(String.format("Animal with ID %s not found.", animal.getId())));

    // Just delete the animal first.
    animalRepository.delete(foundAnimal);

    Map<Barn, List<Animal>> barnToAnimalsMap = getBarnToAnimalsMap(foundAnimal.getFavoriteColor());

    // Count the total animals
    int totalAnimals = barnToAnimalsMap
        .values()
        .stream()
        .mapToInt(List::size)
        .sum();

    if (totalAnimals % FarmUtils.barnCapacity() == 0) {
      // Delete a barn and rebalance
      removeBarnAndRebalanceAnimals(barnToAnimalsMap, animal.getBarn());
    } else {
      // Need to find if we need to move some animals around after the removal
      rebalanceAnimals(barnToAnimalsMap);
    }
  }

  @Override
  public void removeFromFarm(List<Animal> animals) {
    animals.forEach(animal -> removeFromFarm(animalRepository.getOne(animal.getId())));
  }

  private Map<Barn, List<Animal>> getBarnToAnimalsMap(Color color) {
    List<Animal> animals = animalRepository.findByFavoriteColor(color);

    // I'm not using this fun streams method since it doesn't ensure mutability of the collections.
    // return animals.stream().collect(Collectors.groupingBy(Animal::getBarn));

    Map<Barn, List<Animal>> barnToAnimalsMap = new HashMap<>();
    animals
        .forEach(animal -> barnToAnimalsMap.computeIfAbsent(animal.getBarn(), k -> new ArrayList<>()).add(animal));

    return barnToAnimalsMap;
  }

  private void rebalanceAnimals(Map<Barn, List<Animal>> barnToAnimalsMap) {
    List<Animal> updatedAnimals = new ArrayList<>();
    List<Integer> unusedCapacity = getUnusedCapacity(barnToAnimalsMap);

    // Move animals from the most to the least populated barns.
    while (Collections.max(unusedCapacity) - Collections.min(unusedCapacity) > 1) {
      Map.Entry<Barn, List<Animal>> mostPopulatedElement = getMostPopulatedElement(barnToAnimalsMap)
          .orElseThrow(() -> new BadDataException("The map should not be empty at this point."));
      Map.Entry<Barn, List<Animal>> leastPopulatedElement = getLeastPopulatedElement(barnToAnimalsMap)
          .orElseThrow(() -> new BadDataException("The map should not be empty at this point."));

      Animal animal = mostPopulatedElement.getValue().get(0);
      if (animal != null) {
        mostPopulatedElement.getValue().remove(animal);

        animal.setBarn(leastPopulatedElement.getKey());
        leastPopulatedElement.getValue().add(animal);
        updatedAnimals.add(animal);
      }

      unusedCapacity = getUnusedCapacity(barnToAnimalsMap);
    }

    animalRepository.saveAll(updatedAnimals);
  }

  private List<Integer> getUnusedCapacity(Map<Barn, List<Animal>> barnToAnimalsMap) {
    return barnToAnimalsMap.keySet()
        .stream()
        .map(barn -> barn.getCapacity() - barnToAnimalsMap.get(barn).size())
        .collect(Collectors.toList());
  }

  private void removeBarnAndRebalanceAnimals(Map<Barn, List<Animal>> barnToAnimalMap, Barn barn) {
    if (!barnToAnimalMap.isEmpty()) {
      Map.Entry<Barn, List<Animal>> entryToRemove =
          barnToAnimalMap.entrySet()
              .stream()
              .filter(entry -> entry.getKey().getId().equals(barn.getId()))
              .findFirst()
              .orElseThrow(() -> new BadDataException("The map should not be empty at this point."));
      barnToAnimalMap.remove(entryToRemove.getKey());

      entryToRemove.getValue()
          .forEach(animal -> {
            Map.Entry<Barn, List<Animal>> entry = getLeastPopulatedElement(barnToAnimalMap)
                .orElseThrow(() -> new BadDataException("The map should not be empty at this point."));
            animal.setBarn(entry.getKey());
            entry.getValue().add(animal);
          });

      animalRepository.saveAll(entryToRemove.getValue());
    }
    barnRepository.delete(barn);
  }

  private Optional<Map.Entry<Barn, List<Animal>>> getLeastPopulatedElement(Map<Barn, List<Animal>> barnToAnimalMap) {
    return barnToAnimalMap.entrySet().stream().min(BARN_COMPARATOR);
  }

  private Optional<Map.Entry<Barn, List<Animal>>> getMostPopulatedElement(Map<Barn, List<Animal>> barnToAnimalMap) {
    return barnToAnimalMap.entrySet().stream().max(BARN_COMPARATOR);
  }
}
