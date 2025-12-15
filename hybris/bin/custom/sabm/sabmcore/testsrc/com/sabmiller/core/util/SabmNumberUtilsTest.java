package com.sabmiller.core.util;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by zhuo.a.jiang on 19/9/18.
 */
@UnitTest
public class SabmNumberUtilsTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void formattingDouble() throws Exception {
        double doubleValue = 54.08;
        assertEquals("54.08",SabmNumberUtils.formattingDouble(doubleValue));


        doubleValue = 54.0887666;
        assertEquals("54.09",SabmNumberUtils.formattingDouble(doubleValue));

    }

    @Test
    public void formattingDecimal() throws Exception {
        BigDecimal value = new BigDecimal("54.08");
        assertEquals("54.08",SabmNumberUtils.formatDecimal(value));


        BigDecimal value2 = new BigDecimal("54.0887666");
        assertEquals("54.09",SabmNumberUtils.formatDecimal(value2));

    }

}