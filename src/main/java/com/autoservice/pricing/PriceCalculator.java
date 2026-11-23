package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PriceCalculator {

    private final List<PricingRule> rules;

    // Spring автоматично знайде всі класи, що імплементують PricingRule (@Component),
    // і передасть їх сюди у вигляді списку.
    public PriceCalculator(List<PricingRule> rules) {
        this.rules = rules;
    }

    public BigDecimal calculateBaseCost(RepairOrder order) {
        return order.getJobs().stream()
                .map(job -> job.getPartsCost().add(job.getLaborCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateCommercialPrice(RepairOrder order) {
        BigDecimal price = calculateBaseCost(order);
        for (PricingRule rule : rules) {
            price = rule.apply(order, price);
        }
        return price;
    }
}