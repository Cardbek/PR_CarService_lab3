package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import java.math.BigDecimal;

public interface PricingRule {
    BigDecimal apply(RepairOrder order, BigDecimal currentPrice);
}