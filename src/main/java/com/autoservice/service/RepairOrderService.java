package com.autoservice.service;

import com.autoservice.dto.RepairOrderDto;
import com.autoservice.dto.ServiceJobDto;
import com.autoservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface RepairOrderService {
    RepairOrderDto createOrder(String carVin);

    RepairOrderDto getOrderById(UUID id);

    void diagnoseOrder(UUID orderId);

    void addJobsBatch(UUID orderId, List<ServiceJobDto> jobs); // Пакетне додавання (вимога 3 лаби)

    void approveOrder(UUID orderId);

    void startWork(UUID orderId, UUID mechanicId);

    void completeOrder(UUID orderId); // Транзакційне завершення (вимога 3 лаби)

    void cancelOrder(UUID orderId);

    BigDecimal calculateBasePrice(UUID orderId);

    BigDecimal calculateCommercialPrice(UUID orderId);

    List<RepairOrderDto> findOrdersByStatus(OrderStatus status);
}