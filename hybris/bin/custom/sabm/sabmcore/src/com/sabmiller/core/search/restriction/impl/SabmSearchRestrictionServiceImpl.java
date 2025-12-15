package com.sabmiller.core.search.restriction.impl;

import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.search.restriction.impl.DefaultSearchRestrictionService;
import de.hybris.platform.search.restriction.session.SessionSearchRestriction;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SabmSearchRestrictionServiceImpl extends DefaultSearchRestrictionService implements SabmSearchRestrictionService {

    private String SEARCH_RESTRICTION_QUERY = "SELECT {"+SearchRestrictionModel.PK+"} FROM {"+SearchRestrictionModel._TYPECODE+"} WHERE {"+SearchRestrictionModel.CODE+"}=?"+SearchRestrictionModel.CODE;
    private String DUMMY_QUERY = "1=1";
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void simulateSearchRestrictionDisabledInSession(final Set<String> restrictionCodes) {
        if(CollectionUtils.isEmpty(restrictionCodes)){
            return;
        }

        final List<SessionSearchRestriction> searchRestrictions = restrictionCodes.stream()
                .filter(StringUtils::isNotEmpty)
                .map(this::findSearchRestrictionByCode)
                .filter(Optional::isPresent).map(Optional::get)
                .filter(SearchRestrictionModel::getActive).map(this::toSessionSearchRestriction)
                .collect(Collectors.toList());

        if(!searchRestrictions.isEmpty()){
            addSessionSearchRestrictions(searchRestrictions);
        }
    }

    protected SessionSearchRestriction toSessionSearchRestriction(final SearchRestrictionModel searchRestriction){
        return new SessionSearchRestriction(searchRestriction.getCode(),DUMMY_QUERY,searchRestriction.getRestrictedType());
    }

    protected Optional<SearchRestrictionModel> findSearchRestrictionByCode(final String code){
        final SearchResult<SearchRestrictionModel> searchResult = getFlexibleSearchService().search(SEARCH_RESTRICTION_QUERY, Collections.singletonMap(SearchRestrictionModel.CODE,code));
        final List<SearchRestrictionModel> searchRestrictions = searchResult.getResult();
        return CollectionUtils.isEmpty(searchRestrictions)?Optional.empty():Optional.of(searchRestrictions.get(0));
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
