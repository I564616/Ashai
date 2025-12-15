package com.sabmiller.facades.util;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ConfigurableConverters {

    public static <SOURCE, TARGET,OPTION> List<TARGET> convertAllForOptions(final Collection<? extends SOURCE> sourceList,
                                                           final ConfigurableConverter<SOURCE, TARGET,OPTION> configurableConverter,Set<OPTION> options)
    {
        Objects.requireNonNull(configurableConverter);
        Objects.requireNonNull(options);

        if (CollectionUtils.isEmpty(sourceList) || CollectionUtils.isEmpty(options))
        {
            return Collections.emptyList();
        }

        final List<TARGET> result = new ArrayList<>(sourceList.size());

        for (final SOURCE source : sourceList)
        {
            result.add(configurableConverter.convertForOptions(source,options));
        }

        return result;
    }
}
