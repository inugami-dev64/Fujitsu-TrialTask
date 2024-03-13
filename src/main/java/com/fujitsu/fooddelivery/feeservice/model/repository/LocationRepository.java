package com.fujitsu.fooddelivery.feeservice.model.repository;

import com.fujitsu.fooddelivery.feeservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByCity(String city);
    boolean existsByCity(String city);
}
