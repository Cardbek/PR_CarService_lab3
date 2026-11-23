package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimumPricePricingRuleTest {

    private final MinimumPricePricingRule rule = new MinimumPricePricingRule();
    private final RepairOrder dummyOrder = new RepairOrder(); // Правило не використовує order, тому він порожній

    @Test
    void apply_PriceBelowMinimum_ReturnsMinimumPrice() {
        BigDecimal result = rule.apply(dummyOrder, new BigDecimal("300.00"));
        assertEquals(new BigDecimal("500"), result);
    }

    @Test
    void apply_PriceAboveMinimum_ReturnsOriginalPrice() {
        BigDecimal result = rule.apply(dummyOrder, new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("1500.00"), result);
    }

    @Test
    void apply_PriceExactlyMinimum_ReturnsMinimumPrice() {
        BigDecimal result = rule.apply(dummyOrder, new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), result);
    }
}