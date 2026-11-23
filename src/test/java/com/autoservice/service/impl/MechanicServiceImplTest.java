package com.autoservice.service.impl;

import com.autoservice.entity.Mechanic;
import com.autoservice.repository.MechanicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MechanicServiceImplTest {

    @Mock private MechanicRepository mechanicRepository;
    @InjectMocks private MechanicServiceImpl mechanicService;

    @Test
    void saveMechanic_ValidMechanic_ReturnsSavedMechanic() {
        Mechanic mechanic = new Mechanic("Oleg");
        when(mechanicRepository.save(mechanic)).thenReturn(mechanic);

        Mechanic saved = mechanicService.saveMechanic(mechanic);

        assertEquals("Oleg", saved.getName());
        verify(mechanicRepository).save(mechanic);
    }

    @Test
    void getMechanicById_ExistingId_ReturnsMechanic() {
        UUID id = UUID.randomUUID();
        Mechanic mechanic = new Mechanic("Oleg");
        when(mechanicRepository.findById(id)).thenReturn(Optional.of(mechanic));

        Mechanic found = mechanicService.getMechanicById(id);

        assertEquals("Oleg", found.getName());
    }

    @Test
    void getMechanicById_NonExistingId_ThrowsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        when(mechanicRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> mechanicService.getMechanicById(id));
    }

    @Test
    void getAvailableMechanics_CallsRepositoryMethod() {
        when(mechanicRepository.findByBusyFalse()).thenReturn(List.of(new Mechanic("Free Mechanic")));

        List<Mechanic> available = mechanicService.getAvailableMechanics();

        assertEquals(1, available.size());
        assertEquals("Free Mechanic", available.get(0).getName());
        verify(mechanicRepository).findByBusyFalse();
    }
}