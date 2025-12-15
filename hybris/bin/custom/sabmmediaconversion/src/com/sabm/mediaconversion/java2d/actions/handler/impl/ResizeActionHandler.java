package com.sabm.mediaconversion.java2d.actions.handler.impl;

import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.impl.ResizeConversionAction;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Implementation of ResizeAction, basic image resizing capability
 */
public class ResizeActionHandler implements ActionHandler<ResizeConversionAction> {

    private static final int WIDTH_INDEX = 0, HEIGHT_INDEX = 1;
    private static final char OPTION_SIZE_SEPARATOR = 'x';

    @Override public Image convert(BufferedImage image, ResizeConversionAction action) {
        Objects.requireNonNull(image, "Image is required to do conversion.");
        Objects.requireNonNull(action, "action is required to do conversion");

        int[] computedSize = computeFixed(image.getWidth(), image.getHeight(), action.getWidth(), action.getHeight());
        int width = computedSize[WIDTH_INDEX];
        int height = computedSize[HEIGHT_INDEX];

        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    @Override public ResizeConversionAction parseOption(final Option option) {
        Objects.requireNonNull(option, "option cannot be null");
        final String value = option.getValue();

        final String strSize[] = StringUtils.splitPreserveAllTokens(value, OPTION_SIZE_SEPARATOR);

        if(strSize.length != 2) {
            throw new IllegalArgumentException("Invalid option value, expected size of 2 wxh format");
        }

        final String strWidth = strSize[WIDTH_INDEX];
        final String strHeight = strSize[HEIGHT_INDEX];

        if(StringUtils.isBlank(strWidth) && StringUtils.isBlank(strHeight)) {
            throw new IllegalArgumentException("Invalid option value, expected values [wxh | xh | wx]");
        }

        if(!isValidOptionValue(strWidth) || !isValidOptionValue(strHeight)) {
            throw new IllegalArgumentException("Invalid option value, w|h should all be digit");
        }

        return new ResizeConversionAction(NumberUtils.toInt(strWidth, 0), NumberUtils.toInt(strHeight, 0));
    }

    /**
     * Verify if optionValue is valid, empty is considered valid due to config which supports wx | xh, means width will auto proportion height
     *
     * @param optionValue
     * @return
     */
    protected boolean isValidOptionValue(final String optionValue) {
        if(StringUtils.isEmpty(optionValue)) {
            return true;
        }

        return NumberUtils.isDigits(optionValue);
    }

    private int[] computeFixed(int orgWidth, int orgHeight, int targetWidth, int targetHeight) {
        if(!shouldKeepAspectRatio(targetWidth, targetHeight)) {
            return new int[]{targetWidth, targetHeight};
        }

        double percentWidth = targetWidth / orgWidth;
        double percentHeight = targetHeight / orgHeight;

        return computePercentage(orgWidth, orgHeight, percentWidth, percentHeight);
    }

    protected boolean shouldKeepAspectRatio(final int targetWidth, final int targetHeight) {
        return targetWidth <= 0 || targetHeight <= 0;
    }

    private int[] computePercentage(int orgWidth, int orgHeight, double targetWidth, double targetHeight) {
        double width;
        double height;

        if(targetWidth > targetHeight) {
            width = orgWidth * targetWidth;
            height = orgHeight * targetWidth;
        } else {
            width = orgWidth * targetHeight;
            height = orgHeight * targetHeight;
        }
        return new int[]{(int) Math.round(width), (int) Math.round(height)};
    }
}
