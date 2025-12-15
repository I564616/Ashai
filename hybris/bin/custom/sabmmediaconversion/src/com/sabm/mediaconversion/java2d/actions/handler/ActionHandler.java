package com.sabm.mediaconversion.java2d.actions.handler;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import org.apache.commons.cli.Option;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The handler per action type, responsible to doing the conversion and parse the option
 * @param <ACTION>
 */
public interface ActionHandler<ACTION extends ConversionAction> {
    Image convert(final BufferedImage image, ACTION action);

    ACTION parseOption(final Option option);
}
