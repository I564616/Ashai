package com.sabm.mediaconversion.java2d.actions.util.impl;

import com.sabm.mediaconversion.java2d.actions.ConversionAction;
import com.sabm.mediaconversion.java2d.actions.handler.ActionHandler;
import com.sabm.mediaconversion.java2d.actions.util.ActionRegistry;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ActionRegistryImpl implements ActionRegistry, InitializingBean {

    private Map<Class<? extends ConversionAction>, ActionHandler<? extends ConversionAction>> actionHandlerMap;

    @Override public Set<Class<? extends ConversionAction>> getAllConversionActionClasses() {
        return Collections.unmodifiableSet(actionHandlerMap.keySet());
    }

    @Override public <T extends ConversionAction> ActionHandler<T> getActionHandlerForConversionClass(Class<T> conversionActionClass) {
        return (ActionHandler<T>) getActionHandlerMap().get(conversionActionClass);
    }

    protected Map<Class<? extends ConversionAction>, ActionHandler<? extends ConversionAction>> getActionHandlerMap() {
        return actionHandlerMap;
    }

    public void setActionHandlerMap(Map<Class<? extends ConversionAction>, ActionHandler<? extends ConversionAction>> actionHandlerMap) {
        this.actionHandlerMap = actionHandlerMap;
    }

    @Override public void afterPropertiesSet() throws Exception {
        if(actionHandlerMap == null) {
            actionHandlerMap = Collections.emptyMap();
        }
    }
}
