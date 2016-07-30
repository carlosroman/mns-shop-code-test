package com.carlosroman.mks;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShippingCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
    private static final BigDecimal NINETY = new BigDecimal(90).setScale(2, RoundingMode.DOWN);
    private static final BigDecimal FIFTY = new BigDecimal(50).setScale(2, RoundingMode.DOWN);

    public BigDecimal getCostFor(final BigDecimal total) {
        if(NINETY.compareTo(total) > 0) {
            if (FIFTY.compareTo(total) > 0) {
                return new BigDecimal(4.95).setScale(2, RoundingMode.DOWN);
            }
            return new BigDecimal(2.95).setScale(2, RoundingMode.DOWN);
        }
        return ZERO;
    }
}
