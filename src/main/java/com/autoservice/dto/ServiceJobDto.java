package com.autoservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceJobDto {
    private UUID id;
    private String description;
    private BigDecimal partsCost;
    private BigDecimal laborCost;
    private boolean isDone;

    public ServiceJobDto() {
    }

    public ServiceJobDto(String description, BigDecimal partsCost, BigDecimal laborCost) {
        this.description = description;
        this.partsCost = partsCost;
        this.laborCost = laborCost;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(BigDecimal partsCost) {
        this.partsCost = partsCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}