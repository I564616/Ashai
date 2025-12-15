package com.sabm.mediaconversion.java2d.actions.util.impl;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.impl.ResizeConversionAction;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@UnitTest
public class ActionRegistryImplTest {



    @Test
    public void shouldHaveEmptyGivenNullMapOnAfterPropertiesSet() throws Exception{
        final ActionRegistryImpl actionRegistry = new ActionRegistryImpl();
        actionRegistry.afterPropertiesSet();
        Assert.assertNotNull(actionRegistry.getActionHandlerMap());
    }

    @Test
    public void shouldNotResetToEmptyMapGivenActionMap(){

        final ActionRegistryImpl actionRegistry = new ActionRegistryImpl();
        final ActionHandler<ResizeConversionAction> mockActionHandler = Mockito.mock(ActionHandler.class);
        final Map<Class<? extends ConversionAction>, ActionHandler<? extends ConversionAction>> actionHandlerMap = Collections
                .singletonMap(ResizeConversionAction.class,mockActionHandler);
        actionRegistry.setActionHandlerMap(actionHandlerMap);

        Assert.assertNotNull(actionRegistry.getActionHandlerMap());
        Assert.assertEquals(actionHandlerMap,actionRegistry.getActionHandlerMap());
    }

    @Test
    public void shouldReturnAllConversionClassesGivenValidHandlerMap(){

        final ActionRegistryImpl actionRegistry = new ActionRegistryImpl();
        final ActionHandler<ResizeConversionAction> mockActionHandler = Mockito.mock(ActionHandler.class);
        final Map<Class<? extends ConversionAction>, ActionHandler<? extends ConversionAction>> actionHandlerMap = Collections
                .singletonMap(ResizeConversionAction.class,mockActionHandler);
        actionRegistry.setActionHandlerMap(actionHandlerMap);

        Set<Class<? extends ConversionAction>> result = actionRegistry.getAllConversionActionClasses();
        Assert.assertNotNull(result);
        Assert.assertEquals(1,result.size());
        Assert.assertEquals(ResizeConversionAction.class,result.iterator().next());

        Assert.assertNull(actionRegistry.getActionHandlerForConversionClass(null));

        final ActionHandler actionHandler = actionRegistry.getActionHandlerForConversionClass(ResizeConversionAction.class);
        Assert.assertNotNull(actionHandler);
        Assert.assertEquals(mockActionHandler,actionHandler);
    }


}