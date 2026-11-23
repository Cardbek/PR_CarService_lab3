package com.autoservice.dto;

import com.autoservice.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RepairOrderDto {
    private UUID id;
    private String carVin;
    private UUID mechanicId;
    private OrderStatus status;
    private LocalDateTime completedAt;
    private List<ServiceJobDto> jobs;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCarVin() {
        return carVin;
    }

    public void setCarVin(String carVin) {
        this.carVin = carVin;
    }

    public UUID getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(UUID mechanicId) {
        this.mechanicId = mechanicId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<ServiceJobDto> getJobs() {
        return jobs;
    }

    public void setJobs(List<ServiceJobDto> jobs) {
        this.jobs = jobs;
    }
}