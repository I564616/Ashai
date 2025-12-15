package com.sabmiller.core.util;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by zhuo.a.jiang on 17/9/18.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class SabmStringUtilsTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void stripLeadingZeroes() throws Exception {
        assertEquals("8455553", SabmStringUtils.stripLeadingZeroes("0000008455553"));

        assertEquals("8455553", SabmStringUtils.stripLeadingZeroes("0008455553"));
    }

    @Test
    public void addLeadingZeroes() throws Exception {

        // 7 digits b2bUnit Code
        assertEquals("0008455553", SabmStringUtils.addLeadingZeroes("8455553"));

        // 6 digits b2bUnit Code
        assertEquals("0000845555", SabmStringUtils.addLeadingZeroes("845555"));

        // not 7 or 6  digits b2bUnit Code
        assertEquals("84555511111", SabmStringUtils.addLeadingZeroes("84555511111"));

    }


    @Test
    public void convertToInternationalMobileNumber() throws Exception {
        String mobileNumber_AustraliaFormat = "0427092321";

        String businessNumber_AustraliaFormat = "0308981234";


        assertEquals("61427092321", SabmStringUtils.convertToInternationalMobileNumber(mobileNumber_AustraliaFormat));
        assertEquals("61308981234", SabmStringUtils.convertToInternationalMobileNumber(businessNumber_AustraliaFormat));
    }

}