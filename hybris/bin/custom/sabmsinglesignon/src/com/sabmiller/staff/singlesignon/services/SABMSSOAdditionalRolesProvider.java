package com.sabmiller.staff.singlesignon.services;

import java.util.Set;

public interface SABMSSOAdditionalRolesProvider {

    Set<String> getAdditionalRoles(final String id, final String name);
}
