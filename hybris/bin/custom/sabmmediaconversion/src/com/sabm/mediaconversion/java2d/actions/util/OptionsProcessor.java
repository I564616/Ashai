package com.sabm.mediaconversion.java2d.actions.util;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.List;

public interface OptionsProcessor {

    Options provideOptions();

    <T extends ConversionAction> List<T> extractActions(final CommandLine commandLine);
}
