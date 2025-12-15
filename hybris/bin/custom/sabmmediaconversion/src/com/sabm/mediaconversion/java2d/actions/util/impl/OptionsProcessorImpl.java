package com.sabm.mediaconversion.java2d.actions.util.impl;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.annotations.ConversionOption;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.util.ActionRegistry;
import com.sabm.mediaconversion.java2d.actions.util.OptionsProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class OptionsProcessorImpl implements OptionsProcessor {

    private ActionRegistry actionRegistry;

    private Options options;

    @Override public Options provideOptions() {

        if(options != null) {
            return options;
        }

        options = new Options();

        final Set<Class<? extends ConversionAction>> conversionActions = getActionRegistry().getAllConversionActionClasses();

        //add all arguments
        conversionActions.stream().filter(Objects::nonNull).map(this::getConversionOptionFor).forEach(this::addOption);

        return options;
    }


    @Override public List<? extends ConversionAction> extractActions(final CommandLine commandLine) {
        Objects.requireNonNull(commandLine, "commandLine cannot be null.");
        final List<? extends ConversionAction> conversionActions = new ArrayList<>();
        Arrays.stream(commandLine.getOptions()).forEach(option -> {
            conversionActions.add(createConversionActionForOption(option));
        });

        return Collections.unmodifiableList(conversionActions);
    }

    protected <T extends ConversionAction> T  createConversionActionForOption(final Option option) {
        Optional<Class<? extends ConversionAction>> optConversionActionClass = getActionRegistry().getAllConversionActionClasses().stream()
                .filter(conversionActionClass -> isOptionForConversionAction(option, conversionActionClass)).findFirst();

        final Class<? extends ConversionAction> conversionActionClass = optConversionActionClass.orElseThrow(RuntimeException::new);

        return createActionFor(conversionActionClass, option);
    }

    protected <T extends ConversionAction> T createActionFor(final Class<? extends ConversionAction> conversionActionClass, final Option option) {
        final ActionHandler<? extends ConversionAction> actionHandler = getActionRegistry().getActionHandlerForConversionClass(conversionActionClass);
        return (T) actionHandler.parseOption(option);
    }

    protected boolean isOptionForConversionAction(final Option option, final Class<? extends ConversionAction> conversionActionClass) {
        final ConversionOption conversionOption = getConversionOptionFor(conversionActionClass);
        return StringUtils.equals(conversionOption.value(), option.getOpt());
    }

    //this is a stateful method, to avoid errors, using private
    private void addOption(final ConversionOption conversionOption) {
        options.addOption(conversionOption.value(), conversionOption.hasArgument(), conversionOption.description());
    }

    protected <T extends ConversionAction> ConversionOption getConversionOptionFor(Class<T> conversionActionClass) {
        return conversionActionClass.getAnnotation(ConversionOption.class);
    }

    protected ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    public void setActionRegistry(ActionRegistry actionRegistry) {
        this.actionRegistry = actionRegistry;
    }
}
