package com.sabmiller.facades.customer.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by zhuo.a.jiang on 21/9/18.
 */
public class DefaultSABMInvoiceFacadeTest {


    @Test
    public void testCompareDoubleValue (){

        Double doube1 = Double.valueOf("54.01");
        Double doube2 = Double.valueOf("54.01");
        assertEquals(0, Double.compare(doube1, doube2));



        Double doube3 = Double.valueOf("54.01");
        Double doube4 = Double.valueOf("54.011");
        assertNotEquals(0, Double.compare(doube3, doube4));
    }


}