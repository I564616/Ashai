package com.sabm.mediaconversion.java2d.actions.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class ResizeConversionActionTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEOnNewGivenInvalidSizes(){
        new ResizeConversionAction(0,0);
    }

    @Test
    public void shouldCreateNewGivenValidSizes(){
        final ResizeConversionAction result = new ResizeConversionAction(10,0);
        Assert.assertNotNull(result);
        Assert.assertEquals(10,result.getWidth());
        Assert.assertEquals(0,result.getHeight());
    }
}