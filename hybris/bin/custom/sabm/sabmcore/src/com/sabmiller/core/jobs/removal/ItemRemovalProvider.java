package com.sabmiller.core.jobs.removal;

import com.sabmiller.core.model.OldSABMDealsRemovalCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.List;
public interface ItemRemovalProvider<T extends ItemModel,E extends CronJobModel,V> {

    // prepare context
    V prepare(final OldSABMDealsRemovalCronJobModel oldSABMDealsRemovalCronJob);

    List<T> getItemsToRemoved(final E e, V context);

}
