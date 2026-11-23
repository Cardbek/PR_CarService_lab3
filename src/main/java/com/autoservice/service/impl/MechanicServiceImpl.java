package com.autoservice.service.impl;

import com.autoservice.entity.Mechanic;
import com.autoservice.repository.MechanicRepository;
import com.autoservice.service.MechanicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MechanicServiceImpl implements MechanicService {

    private final MechanicRepository mechanicRepository;

    public MechanicServiceImpl(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    @Override
    public Mechanic saveMechanic(Mechanic mechanic) {
        return mechanicRepository.save(mechanic);
    }

    @Override
    public Mechanic getMechanicById(UUID id) {
        return mechanicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mechanic not found."));
    }

    @Override
    public List<Mechanic> getAvailableMechanics() {
        return mechanicRepository.findByBusyFalse(); // Використовуємо метод з репозиторію
    }
}