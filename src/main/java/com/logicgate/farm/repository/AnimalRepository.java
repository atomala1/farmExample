package com.logicgate.farm.repository;

import com.logicgate.farm.domain.Animal;
import com.logicgate.farm.domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
  List<Animal> findByFavoriteColor(final Color color);
}
