package com.sabmiller.core.order.dao;

import de.hybris.platform.acceleratorservices.order.dao.impl.DefaultAcceleratorConsignmentDao;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhuo.a.jiang on 5/02/2018.
 */
public class DefaultSabmConsignmentDao extends DefaultAcceleratorConsignmentDao implements SabmConsignmentDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmConsignmentDao.class);

    @Override
    public List<ConsignmentModel> getConsignmentForStatus(final OrderModel order, final List<ConsignmentStatus> consignmentStatus) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                "select {c.pk} from {" + ConsignmentModel._TYPECODE + "  as c JOIN " + ConsignmentStatus._TYPECODE
                        + "  as cs on {c.status} = {cs.pk}} where {cs.pk} in (?consignmentStatus) AND {c.order} = ?order");
        query.addQueryParameter("order", order);
        query.addQueryParameter("consignmentStatus", consignmentStatus);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Consignment query : {}", query);
        }
        final SearchResult<ConsignmentModel> searchResult = getFlexibleSearchService().search(query);
        return searchResult.getResult();
    }

    @Override
    public List<ConsignmentModel> getConsignmentForCode(final String code){
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                "select {c.pk} from {" + ConsignmentModel._TYPECODE + "  as c } " + " where {c.code} = ?consignmentCode");

        query.addQueryParameter("consignmentCode", code);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Consignment query : {}", query);
        }
        final SearchResult<ConsignmentModel> searchResult = getFlexibleSearchService().search(query);
        return searchResult.getResult();
    }

}
