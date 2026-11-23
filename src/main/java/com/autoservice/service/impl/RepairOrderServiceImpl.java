package com.autoservice.service.impl;

import com.autoservice.dto.RepairOrderDto;
import com.autoservice.dto.ServiceJobDto;
import com.autoservice.entity.*;
import com.autoservice.pricing.PriceCalculator;
import com.autoservice.repository.CarRepository;
import com.autoservice.repository.MechanicRepository;
import com.autoservice.repository.RepairOrderRepository;
import com.autoservice.service.RepairOrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final CarRepository carRepository;
    private final MechanicRepository mechanicRepository;
    private final PriceCalculator priceCalculator;

    public RepairOrderServiceImpl(RepairOrderRepository repairOrderRepository,
                                  CarRepository carRepository,
                                  MechanicRepository mechanicRepository,
                                  PriceCalculator priceCalculator) {
        this.repairOrderRepository = repairOrderRepository;
        this.carRepository = carRepository;
        this.mechanicRepository = mechanicRepository;
        this.priceCalculator = priceCalculator;
    }

    @Override
    @Transactional
    public RepairOrderDto createOrder(String carVin) {
        Car car = carRepository.findById(carVin)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        RepairOrder order = new RepairOrder(car);
        return mapToDto(repairOrderRepository.save(order));
    }

    @Override
    @Transactional
    public RepairOrderDto getOrderById(UUID id) {
        RepairOrder order = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID '" + id + "' was not found."));
        return mapToDto(order);
    }

    @Override
    @Transactional
    public void diagnoseOrder(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only new orders can be diagnosed.");
        }
        order.setStatus(OrderStatus.DIAGNOSED);
        repairOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void addJobsBatch(UUID orderId, List<ServiceJobDto> jobDtos) {
        RepairOrder order = getOrderEntity(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot add jobs to a completed or cancelled order.");
        }

        // Вимога: пакетне додавання. Якщо хоч одна робота з помилкою, уся транзакція відкотиться
        List<ServiceJob> newJobs = jobDtos.stream().map(dto -> {
            if (dto.getPartsCost().compareTo(BigDecimal.ZERO) < 0 || dto.getLaborCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Labor and parts costs cannot be negative.");
            }
            ServiceJob job = new ServiceJob(dto.getDescription(), dto.getPartsCost(), dto.getLaborCost());
            job.setRepairOrder(order);
            return job;
        }).collect(Collectors.toList());

        order.getJobs().addAll(newJobs);
        repairOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void approveOrder(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        if (order.getStatus() != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("An order can only be approved after diagnosis.");
        }
        if (order.getJobs().isEmpty()) {
            throw new IllegalStateException("An order cannot be approved without any service jobs.");
        }
        order.setStatus(OrderStatus.APPROVED);
        repairOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void startWork(UUID orderId, UUID mechanicId) {
        RepairOrder order = getOrderEntity(orderId);
        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> new IllegalArgumentException("Mechanic not found"));

        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new IllegalStateException("Only approved orders can be started.");
        }
        if (mechanic.isBusy()) {
            throw new IllegalStateException("Mechanic " + mechanic.getName() + " is already assigned to another order.");
        }

        mechanic.setBusy(true);
        mechanicRepository.save(mechanic);

        order.setMechanic(mechanic);
        order.setStatus(OrderStatus.IN_PROGRESS);
        repairOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void completeOrder(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress orders can be completed.");
        }

        // Використання Stream API для перевірки (Вимога 3 лаби)
        boolean allDone = order.getJobs().stream().allMatch(ServiceJob::isDone);
        if (!allDone) {
            throw new IllegalStateException("Cannot complete the order because not all jobs are done.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        if (order.getMechanic() != null) {
            order.getMechanic().setBusy(false);
            mechanicRepository.save(order.getMechanic());
        }

        repairOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Completed or cancelled orders cannot be cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getMechanic() != null) {
            order.getMechanic().setBusy(false);
            mechanicRepository.save(order.getMechanic());
        }
        repairOrderRepository.save(order);
    }

    @Override
    @Transactional // Транзакція потрібна для LAZY завантаження jobs
    public BigDecimal calculateBasePrice(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        return order.getJobs().stream()
                .map(job -> job.getPartsCost().add(job.getLaborCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public BigDecimal calculateCommercialPrice(UUID orderId) {
        RepairOrder order = getOrderEntity(orderId);
        return priceCalculator.calculateCommercialPrice(order);
    }

    @Override
    public List<RepairOrderDto> findOrdersByStatus(OrderStatus status) {
        return repairOrderRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RepairOrder getOrderEntity(UUID orderId) {
        return repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private RepairOrderDto mapToDto(RepairOrder entity) {
        RepairOrderDto dto = new RepairOrderDto();
        dto.setId(entity.getId());
        dto.setCarVin(entity.getCar().getVin());
        if (entity.getMechanic() != null) dto.setMechanicId(entity.getMechanic().getId());
        dto.setStatus(entity.getStatus());
        dto.setCompletedAt(entity.getCompletedAt());

        if (entity.getJobs() != null) {
            dto.setJobs(entity.getJobs().stream().map(job -> {
                ServiceJobDto jDto = new ServiceJobDto(job.getDescription(), job.getPartsCost(), job.getLaborCost());
                jDto.setId(job.getId());
                jDto.setDone(job.isDone());
                return jDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}