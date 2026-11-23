package com.autoservice.repository;

import com.autoservice.entity.Car;
import com.autoservice.entity.OrderStatus;
import com.autoservice.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, UUID> {
    List<RepairOrder> findByStatus(OrderStatus status);

    List<RepairOrder> findByCar(Car car);
}