package com.carlosroman.mks;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ShippingCalculatorTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new BigDecimal(90).setScale(2, RoundingMode.DOWN), BigDecimal.ZERO.setScale(2, RoundingMode.DOWN)},
                {new BigDecimal(49.99).setScale(2, RoundingMode.DOWN), new BigDecimal(4.95).setScale(2, RoundingMode.DOWN)},
                {new BigDecimal(50).setScale(2, RoundingMode.DOWN), new BigDecimal(2.95).setScale(2, RoundingMode.DOWN)}
        });
    }

    private final ShippingCalculator undertest = new ShippingCalculator();
    private final BigDecimal total;
    private final BigDecimal expected;

    public ShippingCalculatorTest(final BigDecimal total,
                                  final BigDecimal expected) {
        this.total = total;
        this.expected = expected;
    }

    @Test
    public void test() {
        final BigDecimal actual = undertest.getCostFor(total);
        assertThat(actual)
                .withFailMessage("For total %s, expected shipping of %s, got %s", this.total, this.expected, actual)
                .isEqualTo(this.expected);
    }
}