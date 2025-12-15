package com.apb.core.order.history.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.impl.DefaultOrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Date;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.order.history.service.ApbOrderHistoryService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.apb.core.util.AsahiSiteUtil;


public class ApbOrderHistoryServiceImpl extends DefaultOrderHistoryService implements ApbOrderHistoryService
{
	@Resource
	TypeService typeService;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	private static final Logger LOG = LoggerFactory.getLogger(ApbOrderHistoryServiceImpl.class);

	@Override
	public OrderModel createHistorySnapshot(final OrderModel currentVersion)
	{
		if(!asahiSiteUtil.isCub())
		{
		if (currentVersion == null)
		{
			throw new IllegalArgumentException("current order version was null");

		}
		else if (currentVersion.getVersionID() != null)
		{
			throw new IllegalArgumentException("order is already snapshot");
		}
		else
		{
			final OrderModel copy = this.getModelService().clone(currentVersion);
			copy.setVersionID((String) this.getVersionIDGenerator().generate());
			copy.setOriginalVersion(currentVersion);
			copy.setCode(copy.getCode() + "-" + copy.getVersionID());
			return copy;
		}
		}
		else
		{
			return super.createHistorySnapshot(currentVersion);
		}
	}

	@Override
	public void createOrderHistoryEntry(final OrderModel order, final OrderModel snapshot, final String description)
	{
		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);
		historyEntry.setTimestamp(new Date());
		historyEntry.setOrder(order);
		historyEntry.setDescription(null == description ? null : description);
		historyEntry.setOwner(order.getOwner());
		historyEntry.setPreviousOrderVersion(snapshot);

		getModelService().save(historyEntry);
	}

}
