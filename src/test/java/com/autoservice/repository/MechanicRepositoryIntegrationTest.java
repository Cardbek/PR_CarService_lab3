package com.autoservice.repository;

import com.autoservice.entity.Mechanic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MechanicRepositoryIntegrationTest {

    @Autowired
    private MechanicRepository mechanicRepository;

    @Test
    void findByBusyFalse_ReturnsOnlyAvailableMechanics() {
        // Arrange
        Mechanic m1 = new Mechanic("Ivan");
        m1.setBusy(true);
        Mechanic m2 = new Mechanic("Oleg");
        m2.setBusy(false);
        mechanicRepository.saveAll(List.of(m1, m2));

        // Act
        List<Mechanic> available = mechanicRepository.findByBusyFalse();

        // Assert
        UUID busyId = m1.getId();
        UUID freeId = m2.getId();

        assertTrue(available.stream().anyMatch(m -> m.getId().equals(freeId)),
                "Oleg (вільний механік) повинен бути у списку");
        assertTrue(available.stream().noneMatch(m -> m.getId().equals(busyId)),
                "Ivan (зайнятий механік) НЕ повинен бути у списку");
    }
}