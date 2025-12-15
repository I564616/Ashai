package com.sabmiller.core.util.dao.impl;

import com.sabmiller.core.util.dao.CleanupDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;

public class CleanupDaoImpl implements CleanupDao {

    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;

    protected static final String QUERY = "SELECT {pk} FROM {%s} WHERE {%s} IS NULL";

    /**
     * Note: it's the callers responsibility to make sure that batchSize if greater than zero and a valid reference
     *
     * @param orphanedType
     * @param reference
     * @param batchSize
     * @param <T>
     * @return
     */
    @Override
    public <T extends ItemModel> List<T> getItemsWithEmptyReference(Class<T> orphanedType, String reference, int batchSize) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(createQueryFor(orphanedType, reference));
        searchQuery.setResultClassList(Collections.singletonList(orphanedType));
        searchQuery.setCount(batchSize);
        final SearchResult<T> result = flexibleSearchService.search(searchQuery);
        return result.getResult();
    }

    protected <T extends ItemModel> String createQueryFor(final Class<T> orphanedType, final String reference) {
        return String.format(QUERY, getModelService().getModelType(orphanedType), reference);
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
