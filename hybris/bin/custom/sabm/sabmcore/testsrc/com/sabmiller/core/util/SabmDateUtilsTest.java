package com.sabmiller.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Created by zhuo.a.jiang on 18/01/2018.
 */
public class SabmDateUtilsTest {



    private static final String DATE_SAFE_FORMAT = "yyyy-MM-dd-HH-mm";
    private final SimpleDateFormat format = new SimpleDateFormat(DATE_SAFE_FORMAT);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void roundDateToNearestQuarterHour() throws Exception {
        Date roundedTime;

        Date currentTimeMinutes_53 = format.parse("2018-12-01-12-53");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_53);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-13-00"));
        Assert.assertNotEquals(roundedTime,format.parse("2018-12-01-12-00"));

        Date currentTimeMinutes_07 = format.parse("2018-12-01-12-07");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_07);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-00"));

        Date currentTimeMinutes_59 = format.parse("2018-12-01-12-59");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_59);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-13-00"));
        Assert.assertNotEquals(roundedTime,format.parse("2018-12-01-12-00"));

        Date currentTimeMinutes_08 = format.parse("2018-12-01-12-08");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_08);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-15"));

        Date currentTimeMinutes_22 = format.parse("2018-12-01-12-22");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_22);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-15"));

        Date currentTimeMinutes_20 = format.parse("2018-12-01-12-20");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_20);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-15"));


        Date currentTimeMinutes_23 = format.parse("2018-12-01-12-23");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_23);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-30"));

        Date currentTimeMinutes_37 = format.parse("2018-12-01-12-37");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_37);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-30"));

        Date currentTimeMinutes_32 = format.parse("2018-12-01-12-32");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_32);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-30"));


        Date currentTimeMinutes_38 = format.parse("2018-12-01-12-38");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_38);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-45"));

        Date currentTimeMinutes_52 = format.parse("2018-12-01-12-52");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_52);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-45"));

        Date currentTimeMinutes_41 = format.parse("2018-12-01-12-41");
        roundedTime = SabmDateUtils.roundDateToNearestQuarterHour(currentTimeMinutes_41);
        Assert.assertEquals(roundedTime,format.parse("2018-12-01-12-45"));

    }

}