package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountPricingRuleTest {

    private final DiscountPricingRule rule = new DiscountPricingRule();
    private final RepairOrder dummyOrder = new RepairOrder();

    @Test
    void apply_AnyPrice_AppliesTenPercentDiscount() {
        BigDecimal result = rule.apply(dummyOrder, new BigDecimal("1000.00"));
        // 1000 - 10% = 900.00
        assertEquals(new BigDecimal("900.00"), result);
    }

    @Test
    void apply_ZeroPrice_ReturnsZero() {
        BigDecimal result = rule.apply(dummyOrder, BigDecimal.ZERO);
        assertEquals(new BigDecimal("0.00"), result);
    }
}