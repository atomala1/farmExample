package com.logicgate.farm.service;

import com.logicgate.farm.domain.Animal;

import java.util.List;

/**
 * This is the main service used to control the organization of animals within barns. All methods in this interface must
 * be implemented. Feel free to add any additional methods to this service or inject any other components that you wish
 * to create.
 */
public interface AnimalService {

  /**
   * This method is already implemented. It will return every animal in the database.
   *
   * @return every animal in the database
   */
  List<Animal> findAll();

  /**
   * This method is already implemented. It will remove all animals from the database.
   */
  void deleteAll();

  /**
   * <p>
   * An animal moves to the farm. The new animal must have a barn to inhabitate and the barn must match the animal's
   * favorite color. If no such barn exists, then a new barn must be built to accomodate the animal. When a new barn is
   * created it will be empty and have a capacity of 20 animals. No barn may house an amount of animals that exceed the
   * barn's capacity. Animals must be distributed between barns of the same color to allow for as much extra space
   * as possible with in each barn in order to optimize animal happiness. Keep in mind that when a new barn is added it
   * will require a redistribution of animals in other barns.
   * </p>
   *
   * <p>
   *    Barns [
   *      {color: YELLOW, animals: [20]}
   *    ]
   *
   *    -> addToFarm({favoriteColor: YELLOW})
   *
   *    Barns [
   *      {color: YELLOW, animals: [10]},
   *      {color: YELLOW, animals: [11]}
   *    ]
   *
   *    -> addToFarm({favoriteColor: YELLOW})
   *
   *    Barns [
   *      {color: YELLOW, animals: [11]},
   *      {color: YELLOW, animals: [11]}
   *    ]
   * </p>
   *
   * <p>
   * Take the above example where animals are added to a farm with a single yellow barn. Since the barn already has 20
   * animals inhabitants, a new barn must get created to accomodate the new animal. The animals are redistributed
   * to maximize available space within the barn. When a second animal is added there is enough capacity to accomodate
   * the new animal so no new barns need to be created, but the animal must be added to the barn with fewer inhabitants
   * so the available space continues to be optimized across barns.
   * </p>
   * @param animal the new addition to the farm (it's so excited!) - it can be assumed that the animal will have a
   *               name and favorite color, but no barn
   * @return the persisted animal after adding it to the appropriate barn
   */
  Animal addToFarm(Animal animal);

  /**
   * This method is already implemented. It will iterate over the single animal implementation and is used to test.
   *
   * @param animals a whole bunch of new additions to the farm - the same assumptions can be made about each of these
   *                animals that can be made for a single addition from the previous method
   */
  void addToFarm(List<Animal> animals);

  /**
   * <p>
   * An animal is moving to another farm. Afterwards, barns may need to be reorganized. Barns of the same
   * color should always have an evenly distributed number of animal inhabitants to maximize animal happiness. However,
   * if consolodation allows for all remaining animals of a particular color to be housed in fewer barns while
   * remaining below capacity, then consolodation must occur and the extra barn should be destroyed.
   * </p>
   *
   * <p>
   *    Barns [
   *      {color: RED, animals[15]},
   *      {color: RED, animals[15]},
   *      {color: RED, animals[16]},
   *      {color: RED, animals[16]}
   *    ]
   *
   *    -> removeFromFarm({id: #, name: "eg", color: RED})
   *
   *    Barns [
   *      {color: RED, animals[15]},
   *      {color: RED, animals[15]},
   *      {color: RED, animals[15]},
   *      {color: RED, animals[16]}
   *    ]
   *
   *    -> removeFromFarm({id: #, name: "eg", color: RED})
   *
   *    Barns [
   *      {color: RED, animals[20]},
   *      {color: RED, animals[20]},
   *      {color: RED, animals[20]}
   *    ]
   * </p>
   *
   * <p>
   * Take the above example where animals are removed from a farm. In the first removal, no barns need to be
   * destroyed as the animals could not inhabit fewer barns while keeping barns under capacity. After the second
   * removal, the animals can be housed within 3 barns instead of 4 without going over capacity so one of the
   * barns must be destroyed and the animals must be redistributed.
   * </p>
   * @param animal the sad friend who is moving away
   */
  void removeFromFarm(Animal animal);

  /**
   * This method is already implemented. It will iterate over the single animal implementation and is used to test.
   *
   * @param animals the sad friends who are moving away
   */
  void removeFromFarm(List<Animal> animals);

}
