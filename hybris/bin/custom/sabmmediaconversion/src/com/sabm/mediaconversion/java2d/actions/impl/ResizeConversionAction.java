package com.sabm.mediaconversion.java2d.actions.impl;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.annotations.ConversionOption;

@ConversionOption("resize")
public class ResizeConversionAction implements ConversionAction {

    private int width;
    private int height;

    public ResizeConversionAction(final int width, final int height) {
        this.width = width;
        this.height = height;
        validateInit();
    }

    private void validateInit() {
        if(this.width <= 0 && this.height <= 0) {
            throw new IllegalArgumentException(String.format("width[%d] and height[%d] should be greater than 0", width, height));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}