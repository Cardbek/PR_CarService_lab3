package com.autoservice.service;

import com.autoservice.entity.Mechanic;

import java.util.List;
import java.util.UUID;

public interface MechanicService {
    Mechanic saveMechanic(Mechanic mechanic);

    Mechanic getMechanicById(UUID id);

    List<Mechanic> getAvailableMechanics(); // Метод для пошуку вільних майстрів
}