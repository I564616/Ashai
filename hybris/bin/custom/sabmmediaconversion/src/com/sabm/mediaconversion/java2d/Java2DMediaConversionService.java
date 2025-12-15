package com.sabm.mediaconversion.java2d;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.util.ActionRegistry;
import com.sabm.mediaconversion.java2d.actions.util.OptionsProcessor;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;
import de.hybris.platform.mediaconversion.imagemagick.ImageMagickService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This is the implementation of Java2D, although it implements @{@link ImageMagickService}, it's only to reuse
 * the framework
 */
public class Java2DMediaConversionService implements ImageMagickService {

    private CommandLineParser commandLineParser;
    private OptionsProcessor optionsProcessor;
    private ActionRegistry actionRegistry;

    private static final int INPUT_INDEX = 0;
    private static final int OUTPUT_INDEX = 1;

    private static final String DEFAULT_FORMAT_NAME = "png";

    @Override public void convert(List<String> list) throws IOException {

        Objects.requireNonNull(list,"list cannot be null");

        try {
            final CommandLine commandLine = getCommandLineParser()
                    .parse(getOptionsProcessor().provideOptions(), list.toArray(new String[0]));
            final List<ConversionAction> conversionActions = getOptionsProcessor().extractActions(commandLine);

            if(CollectionUtils.isEmpty(conversionActions)) {
                throw new MediaConversionException(
                        String.format("No conversion actions supported for the current conversion command {%s}", list));
            }

            final String[] arguments = commandLine.getArgs();

            if(arguments == null || arguments.length < 2) {
                throw new MediaConversionException("input and output is required");
            }

            final String input = arguments[INPUT_INDEX];
            final String output = arguments[OUTPUT_INDEX];

            BufferedImage bufferedImage = readImage(input);
            final int imageType = bufferedImage.getType();

            for (ConversionAction conversionAction : conversionActions) {
                final ActionHandler actionHandler = getActionRegistry().getActionHandlerForConversionClass(conversionAction.getClass());
                //it's indeterminate if actionHandler returns a editable image, thus a redraw is required during those cases.
                bufferedImage = redrawIfRequired(actionHandler.convert(bufferedImage, conversionAction), imageType);
            }

            storeConvertedImage(bufferedImage, getFormatName(input, output), output);

        } catch(ParseException | MediaConversionException e) {

            throw new RuntimeException(e);
        }
    }

    protected String getFormatName(final String input, final String output) {

        String extension = FilenameUtils.getExtension(output);
        if(StringUtils.isEmpty(extension)) {
            extension = FilenameUtils.getExtension(input);
        }

        return (StringUtils.isEmpty(extension) ? DEFAULT_FORMAT_NAME : extension).toLowerCase();
    }

    protected void storeConvertedImage(final BufferedImage bufferedImage, final String format, final String output) throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            ImageIO.write(bufferedImage, format, fileOutputStream);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    protected BufferedImage redrawIfRequired(final Image image, final int imageType) {
        if(image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);
        final Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        bufferedImage.flush();
        graphics.dispose();
        return bufferedImage;
    }

    protected BufferedImage readImage(final String imageFile) throws IOException {

        try(FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            return ImageIO.read(fileInputStream);
        } catch(IOException io) {
            throw io;
        }

    }

    @Override public String identify(List<String> list) throws IOException {
        throw new UnsupportedOperationException("identify is currently not supported");
    }

    protected CommandLineParser getCommandLineParser() {
        return commandLineParser;
    }

    public void setCommandLineParser(CommandLineParser commandLineParser) {
        this.commandLineParser = commandLineParser;
    }

    protected OptionsProcessor getOptionsProcessor() {
        return optionsProcessor;
    }

    public void setOptionsProcessor(OptionsProcessor optionsProvider) {
        this.optionsProcessor = optionsProvider;
    }

    protected ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    public void setActionRegistry(ActionRegistry actionRegistry) {
        this.actionRegistry = actionRegistry;
    }
}
