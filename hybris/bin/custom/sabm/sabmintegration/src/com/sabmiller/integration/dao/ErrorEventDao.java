package com.sabmiller.integration.dao;

import com.sabmiller.integration.model.ErrorEventModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorEventDao extends DefaultGenericDao<ErrorEventModel> implements GenericDao<ErrorEventModel> {

    private static final String OLD_ERRORS = "SELECT {pk} FROM {" + ErrorEventModel._TYPECODE + "} WHERE {" +
            ErrorEventModel.CREATIONTIME + "} < ?date ORDER by {"+ErrorEventModel.CREATIONTIME+"} asc";

    public ErrorEventDao() {
        super(ErrorEventModel._TYPECODE);
    }

    public List<ErrorEventModel> findByDate(final Date date){
        final Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(OLD_ERRORS, params);
        query.setCount(Config.getInt("sabm.errorevent.cleanup.count", 100));

        final SearchResult<ErrorEventModel> result = getFlexibleSearchService().search(query);
        return result.getResult();
    }
}
