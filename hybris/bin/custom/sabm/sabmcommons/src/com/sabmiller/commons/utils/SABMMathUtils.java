package com.sabmiller.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SABMMathUtils {

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct){
        return base.multiply(pct).divide(new BigDecimal(100));
    }

    public static BigDecimal round(BigDecimal value , int scale) {
        return value.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    public static long convertBigDecimaltoLong(BigDecimal value)
    {
        BigDecimal newValue = value.setScale(2, RoundingMode.HALF_EVEN);
        newValue = newValue.multiply(new BigDecimal(100));
        return newValue.longValue();
    }

    public static BigDecimal convertLongToBigDecimal(Long value)
    {
        return new BigDecimal(value).divide(new BigDecimal(100));
    }

    public static BigDecimal add(BigDecimal value1,BigDecimal value2)
    {
        return value1.add(value2);
    }

}
