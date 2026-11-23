package com.autoservice.service.impl;

import com.autoservice.dto.ServiceJobDto;
import com.autoservice.entity.Car;
import com.autoservice.entity.RepairOrder;
import com.autoservice.repository.CarRepository;
import com.autoservice.repository.RepairOrderRepository;
import com.autoservice.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@SpringBootTest
@Transactional // Роллбек після кожного тесту, щоб не забруднювати БД
class RepairOrderServiceIntegrationTest {

    @Autowired private RepairOrderService repairOrderService;
    @Autowired private RepairOrderRepository repairOrderRepository;
    @Autowired private CarRepository carRepository;

    @Test
    void addJobsBatch_WhenExceptionThrown_RollsBackTransaction() {
        // Arrange
        String uniqueVin = "TEST_VIN_" + UUID.randomUUID().toString().substring(0, 8);
        Car car = carRepository.save(new Car(uniqueVin, "Toyota", "Camry", 2020));
        RepairOrder order = repairOrderRepository.save(new RepairOrder(car));

        List<ServiceJobDto> jobs = List.of(
                new ServiceJobDto("Good Job", BigDecimal.TEN, BigDecimal.TEN),
                new ServiceJobDto("Bad Job", new BigDecimal("-50"), BigDecimal.TEN)
        );

        // Act
        UUID orderId = order.getId();
        assertThrows(IllegalArgumentException.class, () -> repairOrderService.addJobsBatch(orderId, jobs));

        // Assert
        RepairOrder fetchedOrder = repairOrderRepository.findById(orderId).orElseThrow();
        assertTrue(fetchedOrder.getJobs().isEmpty(), "Transaction did not rollback, jobs were saved!");
    }

    @Test
    void completeOrder_UpdatesOrderStatusAndCascadeSaves() {
        // Arrange: Перевірка каскадного збереження та зв'язків Hibernate
        Car car = new Car("String uniqueVin = \"TEST_VIN_\" + UUID.randomUUID().toString().substring(0, 8);                         ", "BMW", "X5", 2021);
        carRepository.save(car);

        var orderDto = repairOrderService.createOrder(car.getVin());
        repairOrderService.diagnoseOrder(orderDto.getId());

        List<ServiceJobDto> jobs = List.of(new ServiceJobDto("Diagnostics", BigDecimal.TEN, BigDecimal.TEN));
        repairOrderService.addJobsBatch(orderDto.getId(), jobs);

        // Assert
        RepairOrder savedOrder = repairOrderRepository.findById(orderDto.getId()).orElseThrow();
        assertEquals(1, savedOrder.getJobs().size(), "Cascade save failed for ServiceJobs");
        assertNotNull(savedOrder.getJobs().get(0).getId(), "ServiceJob ID was not generated");
    }
}