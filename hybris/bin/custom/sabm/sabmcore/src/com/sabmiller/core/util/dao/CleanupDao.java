package com.sabmiller.core.util.dao;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;

public interface CleanupDao {

    <T extends ItemModel> List<T> getItemsWithEmptyReference(final Class<T> orphanedType, final String reference, int batchSize);
}
