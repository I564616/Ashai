package com.sabmiller.facades.util;

import java.util.Set;

public interface ConfigurableConverter<SOURCE,TARGET,OPTION> {

    TARGET convertForOptions(final SOURCE source,final Set<OPTION> option);
}
