package com.sabmiller.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by zhuo.a.jiang on 19/9/18.
 */
public class SabmNumberUtils {




    private SabmNumberUtils() {
        //To Prevent instantiation
    }

    public static String formattingDouble(final Double value) {
        DecimalFormat df = new DecimalFormat("#0.##");

        return df.format(value);

    }

    public static String formatDecimal(final BigDecimal value)
    {
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat.format(value);
    }





}
