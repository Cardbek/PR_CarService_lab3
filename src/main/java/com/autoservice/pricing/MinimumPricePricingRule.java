package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinimumPricePricingRule implements PricingRule {

    private final BigDecimal minPrice = new BigDecimal("500"); // Мінімальна ціна 500

    @Override
    public BigDecimal apply(RepairOrder order, BigDecimal currentPrice) {
        return currentPrice.max(minPrice);
    }
}