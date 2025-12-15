package com.sabm.mediaconversion.java2d.actions.util.impl;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.handler.impl.ResizeActionHandler;
import com.sabm.mediaconversion.java2d.actions.impl.ResizeConversionAction;
import com.sabm.mediaconversion.java2d.actions.util.ActionRegistry;
import com.sabm.mediaconversion.java2d.actions.util.OptionsProcessor;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.validation.constraints.Null;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@UnitTest
public class OptionsProcessorImplTest {


    @Mock
    private ActionRegistry actionRegistry;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnOptionsBasedOnAction(){
        final OptionsProcessorImpl optionsProcessor = createOptionsProcessorImpl();
        final Set<Class<? extends ConversionAction>> allConversionActions = Collections.singleton(ResizeConversionAction.class);
        Mockito.when(actionRegistry.getAllConversionActionClasses()).thenReturn(allConversionActions);
        final Options options = optionsProcessor.provideOptions();
        Assert.assertNotNull(options);
        Assert.assertEquals(1,options.getOptions().size());

        //call again
        optionsProcessor.provideOptions();

        //verify that this is only called once, since options should have been set even if provideOptions is called twice
        Mockito.verify(actionRegistry,Mockito.times(1)).getAllConversionActionClasses();

    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEGivenNullCommandLine(){
        final OptionsProcessor optionsProcessor = createOptionsProcessorImpl();
        optionsProcessor.extractActions(null);
    }


    @Test
    public void shouldExtractActionsGivenRegisteredConversionAction(){
        final OptionsProcessorImpl optionsProcessor = createOptionsProcessorImpl();
        final Set<Class<? extends ConversionAction>> allConversionActions = Collections.singleton(ResizeConversionAction.class);
        final CommandLine commandLine = Mockito.mock(CommandLine.class);
        final Option resizeOption = Mockito.mock(Option.class);
        final Option options[] = new Option[]{resizeOption};
        final ActionHandler actionHandler = new ResizeActionHandler(); // no mocking. :) trust this code.

        Mockito.when(actionRegistry.getAllConversionActionClasses()).thenReturn(allConversionActions);
        Mockito.when(resizeOption.getValue()).thenReturn("90x70");
        Mockito.when(resizeOption.getOpt()).thenReturn("resize");
        Mockito.when(commandLine.getOptions()).thenReturn(options);
        Mockito.when(actionRegistry.getActionHandlerForConversionClass(ResizeConversionAction.class)).thenReturn(actionHandler);


        final List<? extends ConversionAction> result = optionsProcessor.extractActions(commandLine);

        Assert.assertNotNull(result);
        Assert.assertEquals(1,result.size());

        final ConversionAction action = result.get(0);

        Assert.assertNotNull(action);
        Assert.assertTrue(action instanceof ResizeConversionAction);

        final ResizeConversionAction resizeConversionAction = (ResizeConversionAction) action;

        Assert.assertEquals(90,resizeConversionAction.getWidth());
        Assert.assertEquals(70,resizeConversionAction.getHeight());
    }


    private OptionsProcessorImpl createOptionsProcessorImpl(){
        final OptionsProcessorImpl optionsProcessor = new OptionsProcessorImpl();
        optionsProcessor.setActionRegistry(actionRegistry);

        return optionsProcessor;
    }

}