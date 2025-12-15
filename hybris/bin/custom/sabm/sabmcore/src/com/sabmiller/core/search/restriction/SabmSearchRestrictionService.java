package com.sabmiller.core.search.restriction;

import de.hybris.platform.search.restriction.SearchRestrictionService;

import java.util.Set;

public interface SabmSearchRestrictionService extends SearchRestrictionService {

    void simulateSearchRestrictionDisabledInSession(final Set<String> restrictionCodes);
}
