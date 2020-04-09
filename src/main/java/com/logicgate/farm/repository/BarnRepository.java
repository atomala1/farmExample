package com.logicgate.farm.repository;

import com.logicgate.farm.domain.Barn;
import com.logicgate.farm.domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarnRepository extends JpaRepository<Barn, Long> {
}
