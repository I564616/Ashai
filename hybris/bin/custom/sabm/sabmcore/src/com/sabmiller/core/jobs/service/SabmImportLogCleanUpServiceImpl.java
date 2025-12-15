/**
 *
 */
package com.sabmiller.core.jobs.service;

import com.sabmiller.webservice.model.MasterImportModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * @author iqbal.javed
 *
 */
public class SabmImportLogCleanUpServiceImpl implements SabmImportLogCleanUpService {

    private static final String MASTER_IMPORT_LOG_QUERY = "select {pk} from {masterimport} where {creationtime} < ?"+MasterImportModel.CREATIONTIME;

    private FlexibleSearchService flexibleSearchService;
    private static final Logger LOG = LoggerFactory.getLogger(SabmImportLogCleanUpServiceImpl.class.getName());

    public List<MasterImportModel> getMasterImportLogs() {

        final Date sevenDaysAgo = getCurrentSystemDateWithMinus(7);
        final FlexibleSearchQuery fsq = new FlexibleSearchQuery(MASTER_IMPORT_LOG_QUERY, Collections.singletonMap(MasterImportModel.CREATIONTIME, sevenDaysAgo));
        final SearchResult<MasterImportModel> result = this.flexibleSearchService.search(fsq);

        LOG.debug("Count for Master Import:: " + result.getCount());
        return result.getCount() > 0 ? result.getResult() : Collections.emptyList();

    }

    /**
     * @return the flexibleSearchService
     */
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     *           the flexibleSearchService to set
     */
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public String getCurrentSystemDate() {
        final Date currentDate = new Date();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        final String todayDate = simpleDateFormat.format(currentDate);
        return todayDate;

    }

    public Date getCurrentSystemDateWithMinus(Integer days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -days);
        return DateUtils.truncate(c.getTime(), Calendar.DATE);
    }


}
