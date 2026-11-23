package com.autoservice.pricing;

import com.autoservice.entity.RepairOrder;
import com.autoservice.entity.ServiceJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceCalculatorTest {

    @Mock
    private PricingRule mockRule1;
    @Mock
    private PricingRule mockRule2;

    @Test
    void calculateBaseCost_WithMultipleJobs_ReturnsExactSum() {
        // Arrange
        PriceCalculator calculator = new PriceCalculator(List.of());
        RepairOrder order = new RepairOrder();
        order.setJobs(List.of(
                new ServiceJob("Job 1", new BigDecimal("100"), new BigDecimal("50")),
                new ServiceJob("Job 2", new BigDecimal("200"), new BigDecimal("100"))
        ));

        // Act
        BigDecimal baseCost = calculator.calculateBaseCost(order);

        // Assert: (100+50) + (200+100) = 450
        assertEquals(new BigDecimal("450"), baseCost);
    }

    @Test
    void calculateCommercialPrice_AppliesAllRulesInOrder() {
        // Arrange
        PriceCalculator calculator = new PriceCalculator(List.of(mockRule1, mockRule2));
        RepairOrder order = new RepairOrder();
        order.setJobs(List.of(new ServiceJob("Job 1", new BigDecimal("100"), new BigDecimal("100")))); // Сума 200

        when(mockRule1.apply(eq(order), eq(new BigDecimal("200")))).thenReturn(new BigDecimal("500"));
        when(mockRule2.apply(eq(order), eq(new BigDecimal("500")))).thenReturn(new BigDecimal("450"));

        // Act
        BigDecimal commercialPrice = calculator.calculateCommercialPrice(order);

        // Assert
        assertEquals(new BigDecimal("450"), commercialPrice);
        verify(mockRule1).apply(any(), any());
        verify(mockRule2).apply(any(), any());
    }
}