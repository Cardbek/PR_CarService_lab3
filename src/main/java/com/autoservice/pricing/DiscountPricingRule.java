package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DiscountPricingRule implements PricingRule {

    private final BigDecimal discountPercentage = new BigDecimal("10"); // 10% знижки

    @Override
    public BigDecimal apply(RepairOrder order, BigDecimal currentPrice) {
        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );
        return currentPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}