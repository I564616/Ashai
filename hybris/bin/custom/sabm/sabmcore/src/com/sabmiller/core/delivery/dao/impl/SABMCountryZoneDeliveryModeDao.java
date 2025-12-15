/**
 *
 */
package com.sabmiller.core.delivery.dao.impl;

import de.hybris.platform.commerceservices.delivery.dao.impl.DefaultCountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;


/**
 * SABMCountryZoneDeliveryModeDao
 *
 * @author yaopeng
 *
 */
public class SABMCountryZoneDeliveryModeDao extends DefaultCountryZoneDeliveryModeDao
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * Method for find DeliveryModes by FlexibleSearchQuery
	 *
	 * @see de.hybris.platform.commerceservices.delivery.dao.impl.DefaultCountryZoneDeliveryModeDao#findDeliveryModes(de.
	 * hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public Collection<DeliveryModeModel> findDeliveryModes(final AbstractOrderModel abstractOrder)
	{
		
		if(asahiSiteUtil.isCub())
		{
		
   		final StringBuilder query = new StringBuilder("SELECT DISTINCT {zdm:").append(ItemModel.PK).append("}");
   		query.append(" FROM { ").append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS val");
   		query.append(" JOIN ").append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm");
   		query.append(" ON {val:").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("}={zdm:").append(ItemModel.PK);
   		query.append(" } } WHERE {zdm:").append(ZoneDeliveryModeModel.ACTIVE).append("}=?active");
   		query.append(" AND {zdm:").append(ZoneDeliveryModeModel.NET).append("}=?net");
   		query.append(" AND {val:").append(ZoneDeliveryModeValueModel.CURRENCY).append("}=?currency");
   		final Map<String, Object> params = new HashMap<String, Object>();
   		params.put("active", Boolean.TRUE);
   		params.put("net", abstractOrder.getNet());
   		params.put("currency", abstractOrder.getCurrency());
   		return doSearch(query.toString(), params, DeliveryModeModel.class);
		}
		else
		{
			return super.findDeliveryModes(abstractOrder);
		}

	}



}
