package com.sabmiller.staff.singlesignon.services.impl;

import com.sabmiller.staff.singlesignon.services.SABMSSOAdditionalRolesProvider;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SABMSSOAdditionalRolesProviderImpl implements SABMSSOAdditionalRolesProvider {

    private ConfigurationService configurationService;
    protected static final String SABMSINGLESIGNON_ADDITIONAL_ROLES = "sabmsinglesignon.additional.roles";

    @Override public Set<String> getAdditionalRoles(String id, String name) {

        final String[] additionalRoles = getConfigurationService().getConfiguration().getStringArray(SABMSINGLESIGNON_ADDITIONAL_ROLES);

        if(ArrayUtils.isEmpty(additionalRoles)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(additionalRoles)));

    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
