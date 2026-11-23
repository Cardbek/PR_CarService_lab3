package com.autoservice.repository;

import com.autoservice.entity.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MechanicRepository extends JpaRepository<Mechanic, UUID> {
    @Query("SELECT m FROM Mechanic m WHERE m.isBusy = false")
    List<Mechanic> findByBusyFalse();
}