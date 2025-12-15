package com.sabm.mediaconversion.java2d.actions.util;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;

import java.util.Set;

public interface ActionRegistry {

    Set<Class<? extends ConversionAction>> getAllConversionActionClasses();

   <T extends ConversionAction> ActionHandler<T> getActionHandlerForConversionClass(Class<T> conversionActionClass);

}
