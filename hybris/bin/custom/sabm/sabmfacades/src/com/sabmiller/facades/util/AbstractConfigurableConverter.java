package com.sabmiller.facades.util;

import de.hybris.platform.converters.ConfigurablePopulator;

import java.util.Set;

public class AbstractConfigurableConverter<SOURCE,TARGET,OPTION> implements ConfigurableConverter<SOURCE,TARGET,OPTION>{

    private ConfigurablePopulator<SOURCE,TARGET,OPTION> configurablePopulator;
    private Class<TARGET> targetClass;

    protected TARGET createFromClass()
    {
        try
        {
            return getTargetClass().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TARGET convertForOptions(SOURCE source, Set<OPTION> options) {
        TARGET target = createFromClass();
        configurablePopulator.populate(source,target,options);
        return target;
    }

    protected ConfigurablePopulator<SOURCE, TARGET, OPTION> getConfigurablePopulator() {
        return configurablePopulator;
    }

    public void setConfigurablePopulator(ConfigurablePopulator<SOURCE, TARGET, OPTION> configurablePopulator) {
        this.configurablePopulator = configurablePopulator;
    }

    protected Class<TARGET> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<TARGET> targetClass) {
        this.targetClass = targetClass;
    }
}
