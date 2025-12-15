package com.sabm.mediaconversion.java2d.actions.handler.impl;

import com.sabm.mediaconversion.java2d.actions.impl.ResizeConversionAction;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.cli.Option;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;

@UnitTest public class ResizeActionHandlerTest {

    private ResizeActionHandler resizeActionHandler;

    @Before public void setup() {
        resizeActionHandler = new ResizeActionHandler();
    }

    @Test(expected = NullPointerException.class) public void shouldThrowNPEGivenNullImageOnConvert() {

        final ResizeConversionAction resizeConversionAction = Mockito.mock(ResizeConversionAction.class);
        resizeActionHandler.convert(null, resizeConversionAction);
    }

    @Test(expected = NullPointerException.class) public void shouldThrowNPEGivenNullActionOnConvert() {

        final BufferedImage image = Mockito.mock(BufferedImage.class);
        resizeActionHandler.convert(image, null);
    }

    @Test public void shouldConvertGivenFixedWidthHeight() {
        final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class);
        final ResizeConversionAction resizeConversionAction = new ResizeConversionAction(100, 100);

        resizeActionHandler.convert(bufferedImage, resizeConversionAction);

    }

    @Test public void shouldConvertGivenWidthOnly() {
        final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class);
        final ResizeConversionAction resizeConversionAction = new ResizeConversionAction(100, 0);

        Mockito.when(bufferedImage.getWidth()).thenReturn(100);
        Mockito.when(bufferedImage.getHeight()).thenReturn(90);
        resizeActionHandler.convert(bufferedImage, resizeConversionAction);

    }

    @Test public void shouldConvertGivenHeightOnly() {
        final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class);
        final ResizeConversionAction resizeConversionAction = new ResizeConversionAction(0, 100);

        Mockito.when(bufferedImage.getWidth()).thenReturn(100);
        Mockito.when(bufferedImage.getHeight()).thenReturn(90);
        resizeActionHandler.convert(bufferedImage, resizeConversionAction);

    }


    @Test(expected = NullPointerException.class) public void shouldThrowNPEOnParseOption() {
        resizeActionHandler.parseOption(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEGivenOptionWithNoSeparator(){

        final Option option = Mockito.mock(Option.class);

        Mockito.when(option.getValue()).thenReturn("90");
        resizeActionHandler.parseOption(option);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEGivenOptionInvalidValues(){

        final Option option = Mockito.mock(Option.class);

        Mockito.when(option.getValue()).thenReturn("90xasla");
        resizeActionHandler.parseOption(option);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEGivenOptionWithXOnly(){

        final Option option = Mockito.mock(Option.class);

        Mockito.when(option.getValue()).thenReturn("x");
        resizeActionHandler.parseOption(option);
    }

    @Test
    public void shouldParseOptionGivenOnlyWidth(){

        final Option option = Mockito.mock(Option.class);

        Mockito.when(option.getValue()).thenReturn("90x");
        final ResizeConversionAction resizeConversionAction = resizeActionHandler.parseOption(option);

        Assert.assertNotNull(resizeConversionAction);
        Assert.assertEquals(90,resizeConversionAction.getWidth());
        Assert.assertEquals(0,resizeConversionAction.getHeight());
    }

    @Test
    public void shouldParseOptionGivenOnlyHeight(){

        final Option option = Mockito.mock(Option.class);

        Mockito.when(option.getValue()).thenReturn("x90");
        final ResizeConversionAction resizeConversionAction = resizeActionHandler.parseOption(option);

        Assert.assertNotNull(resizeConversionAction);
        Assert.assertEquals(0,resizeConversionAction.getWidth());
        Assert.assertEquals(90,resizeConversionAction.getHeight());
    }

}