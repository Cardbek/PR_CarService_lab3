package com.autoservice.service.impl;

import com.autoservice.dto.ServiceJobDto;
import com.autoservice.entity.Car;
import com.autoservice.entity.Mechanic;
import com.autoservice.entity.OrderStatus;
import com.autoservice.entity.RepairOrder;
import com.autoservice.entity.ServiceJob;
import com.autoservice.repository.CarRepository;
import com.autoservice.repository.MechanicRepository;
import com.autoservice.repository.RepairOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceImplTest {

    @Mock private RepairOrderRepository repairOrderRepository;
    @Mock private CarRepository carRepository;
    @Mock private MechanicRepository mechanicRepository;

    @InjectMocks private RepairOrderServiceImpl repairOrderService;

    // --- State Transitions ---

    @Test
    void diagnoseOrder_StatusCreated_ChangesStatusToDiagnosed() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.CREATED);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        repairOrderService.diagnoseOrder(UUID.randomUUID());

        assertEquals(OrderStatus.DIAGNOSED, order.getStatus());
        verify(repairOrderRepository).save(order);
    }

    @Test
    void diagnoseOrder_StatusNotCreated_ThrowsIllegalStateException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.IN_PROGRESS);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> repairOrderService.diagnoseOrder(UUID.randomUUID()));
    }

    // --- Batch Add Jobs ---

    @Test
    void addJobsBatch_ValidJobs_AddsToOrder() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.CREATED);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        List<ServiceJobDto> jobs = List.of(new ServiceJobDto("Test", BigDecimal.TEN, BigDecimal.TEN));

        repairOrderService.addJobsBatch(UUID.randomUUID(), jobs);

        assertEquals(1, order.getJobs().size());
        verify(repairOrderRepository).save(order);
    }

    @Test
    void addJobsBatch_NegativeCost_ThrowsIllegalArgumentException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.CREATED);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        List<ServiceJobDto> jobs = List.of(new ServiceJobDto("Test", new BigDecimal("-10"), BigDecimal.TEN));

        assertThrows(IllegalArgumentException.class, () -> repairOrderService.addJobsBatch(UUID.randomUUID(), jobs));
    }

    @Test
    void addJobsBatch_OrderCompleted_ThrowsIllegalStateException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.COMPLETED);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> repairOrderService.addJobsBatch(UUID.randomUUID(), List.of()));
    }

    // --- Approval & Completion ---

    @Test
    void approveOrder_NoJobs_ThrowsIllegalStateException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.DIAGNOSED);
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> repairOrderService.approveOrder(UUID.randomUUID()));
    }

    @Test
    void completeOrder_NotAllJobsDone_ThrowsIllegalStateException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setJobs(List.of(new ServiceJob("Job", BigDecimal.TEN, BigDecimal.TEN))); // isDone = false by default
        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> repairOrderService.completeOrder(UUID.randomUUID()));
    }

    @Test
    void startWork_MechanicBusy_ThrowsIllegalStateException() {
        RepairOrder order = new RepairOrder();
        order.setStatus(OrderStatus.APPROVED);
        Mechanic mechanic = new Mechanic("Ivan");
        mechanic.setBusy(true);

        when(repairOrderRepository.findById(any())).thenReturn(Optional.of(order));
        when(mechanicRepository.findById(any())).thenReturn(Optional.of(mechanic));

        assertThrows(IllegalStateException.class, () -> repairOrderService.startWork(UUID.randomUUID(), UUID.randomUUID()));
    }
}